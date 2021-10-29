package ConverterAFN_AFD;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import Estruturas.Automato;
import Estruturas.Estado;
import Estruturas.EstadoFinal;
import Estruturas.Transicao;
import LerEscreverXml.GerarXML;
import LerEscreverXml.LerXml;

public class Principal  {

    private static Automato automato_afn = new Automato();

    private static Automato auto_afd = new Automato();
    private static List<Estado> estados_afd = new ArrayList<>();
    private static List<Transicao> transicoes_afd = new ArrayList<>();
    private static List<EstadoFinal> estadosFinaisAFD = new ArrayList<>();
    private static String Alfabeto_afd = automato_afn.getAlfabeto();
    private static String alfabeto[];

    private static void constroiEstados(String estado) {
        int i, j, k;
        String novoEstado, estadoFinal;
        String estados[];
        List<Transicao> transicoesAFND = automato_afn.getTransition();
        
        // Percorre uma vez para cada símbolo do alfabeto
        for (j = 0; j < alfabeto.length; j++) {
            novoEstado = "";
            estados = estado.split(",");

            // Percorre os estados recebidos como parâmetro para encontrar o novo estado
            for (k = 0; k < estados.length; k++) {
                // Percorre a tabela de transições do AFND
                for (i = 0; i < transicoesAFND.size(); i++) {
                    // Encontra a linha onde o estado de origem é o estado que está sendo testado
                    if (localizarTransicao(transicoesAFND.get(i).getFrom(), automato_afn).equals(estados[k]) && transicoesAFND.get(i).getRead().equals(alfabeto[j])) {
                        // Somente inclui em novoEstado se novoEstado não contém o estado
                        if (!contemEstado(novoEstado, localizarTransicao(transicoesAFND.get(i).getTo(), automato_afn))) {
                            if (novoEstado != "") {
                                novoEstado += ",";
                            }
                            novoEstado += localizarTransicao(transicoesAFND.get(i).getTo(), automato_afn);
                        }
                    }
                }
            }

            // Se não encontrou novoEstado então vai para a próxima iteração
            if (novoEstado == "") {
                continue;
            }

            novoEstado = ordenarEstado(novoEstado);

            boolean verifica = false;
            // Verifica se novoEstado já existe na tabela AFD
            for (i = 0; i < transicoes_afd.size(); i++) {
                if (transicoes_afd.get(i).getFrom() == retornaIdEstado(estado)
                        && transicoes_afd.get(i).getRead().equals(alfabeto[j])
                        && transicoes_afd.get(i).getTo() == retornaIdEstado(novoEstado)) {
                    verifica = true;
                    break;
                }
            }

            // Se novoEstado ainda não existe no AFD, então adiciona e constrói novos estados
            if (!verifica) {
                Estado newEstado = new Estado();
                Transicao newTransicao = new Transicao();
                EstadoFinal newEstadoFinal = new EstadoFinal();
                newEstado.setId(estados_afd.size());
                newEstado.setName(estado);
                newEstado = adicionarEstado(newEstado);

                newEstado.getName().replaceAll("q", "");
                
                newEstado = new Estado();
                newEstado.setId(estados_afd.size());
                newEstado.setName(novoEstado);
                newEstado = adicionarEstado(newEstado);

                newEstado.getName().replaceAll("q", "");

                newTransicao.setFrom(retornaIdEstado(estado));
                newTransicao.setTo(retornaIdEstado(novoEstado));
                newTransicao.setRead(alfabeto[j]);
                transicoes_afd.add(newTransicao);
                estadoFinal = estadoFinalAFND(novoEstado);
                
                if (estadoFinal.equals("*")) {
                    if (!verficaEstadoFinalExiste(novoEstado)) {
                        newEstadoFinal.setId(retornaIdEstado(novoEstado));
                        newEstadoFinal.setName(novoEstado);
                        estadosFinaisAFD.add(newEstadoFinal);
                    }
                }

                constroiEstados(novoEstado);
            }
        }

        auto_afd.setAlfabeto(Alfabeto_afd);
        auto_afd.setEstado(estados_afd);
        auto_afd.setEstadofinal(estadosFinaisAFD);
        auto_afd.setTransition(transicoes_afd);
        auto_afd.setEstadoincial(automato_afn.getEstadoinicial());
    }

    private static String localizarTransicao(int id, Automato auto) {
        List<Estado> e = auto.getEstado();
        for (int i = 0; i < e.size(); i++) {
            if (e.get(i).getId() == id) {
                return e.get(i).getName();
            }
        }
        return "";
    }

    private static boolean verficaEstadoFinalExiste(String nome) {
        boolean verifica = false;
        for (int i = 0; i < estadosFinaisAFD.size(); i++) {
            if ((nome.equals(estadosFinaisAFD.get(i).getName()))) {
                verifica = true;
            }
        }
        return verifica;
    }

    private static int retornaIdEstado(String nome) {
        int id = -1;
        for (int i = 0; i < estados_afd.size(); i++) {
            if ((nome.equals(estados_afd.get(i).getName()))) {
                id = estados_afd.get(i).getId();
            }
        }
        return id;
    }

    private static Estado adicionarEstado(Estado e) {
        boolean verifica = false;
        for (int i = 0; i < estados_afd.size(); i++) {
            if ((e.getName().equals(estados_afd.get(i).getName()))) {
                e = estados_afd.get(i);
                return e;
            }
        }
        if (!verifica) {
            estados_afd.add(e);
        }
        return e;
    }

    private static boolean contemEstado(String estado1, String estado2) {
        int i;
        String estados[] = estado1.split(",");
        for (i = 0; i < estados.length; i++) {
            if (estados[i].equals(estado2)) {
                break;
            }
        }
        if (i < estados.length) {
            return true;
        } else {
            return false;
        }
    }

    private static String ordenarEstado(String estado) {
        String tiraVirgula[] = estado.split(",");
        String tiraVirgula2 = "";
        for (int i = 0; i < tiraVirgula.length; i++) {
            tiraVirgula2 += tiraVirgula[i];
        }
        String tiraQ[] = tiraVirgula2.split("q");
        Arrays.sort(tiraQ, 0, tiraQ.length);
        String addVirgula = "";

        for (int i = 0; i < tiraQ.length; i++) {
            if (!tiraQ[i].equals("")) {
                if (i != (tiraQ.length - 1)) {
                    addVirgula += "q" + tiraQ[i] + ",";
                } else {
                    addVirgula += "q" + tiraQ[i];
                }
            }
        }
        return addVirgula;
    }

    private static String estadoFinalAFND(String s) {
        int i, j;
        String estadosFinais[] = new String[automato_afn.getEstadofinal().size()];
        int o = 0;
        for(EstadoFinal a: automato_afn.getEstadofinal()){
            estadosFinais[o] = a.getName();
            o++;
        }
        String estadosAux[] = s.split(",");
        // Verifica se o estado final é um dos estados finais do automato
        for (i = 0; i < estadosFinais.length; i++) {
            for (j = 0; j < estadosAux.length; j++) {
                if (estadosFinais[i].equals(estadosAux[j])) {
                    return "*";
                }
            }
        }
        return "";
    }

    private static String proximoEstado(String p1, String p2) {
        int i;
        String s = null;
        // Encontra uma entrada na tabela de transição de estados
        for (i = 0; i < transicoes_afd.size(); i++) {
            if (transicoes_afd.get(i).getFrom() == (retornaIdEstado(p1))
                    && transicoes_afd.get(i).getRead().equals(p2)) {
                s = localizarTransicao(transicoes_afd.get(i).getTo(), auto_afd);
                break;
            }
        }
        return s;
    }

    private static boolean estadoFinalAFD(String s) {
        int i;
        for (i = 0; i < estadosFinaisAFD.size(); i++) {
            if (estadosFinaisAFD.get(i).getId() == retornaIdEstado(s)) {
                return true;
            }
        }
        return false;
    }

    private static boolean verificaSentenca(Automato aut, String sentenca) {
        int i;
        
        transicoes_afd.size();
        // Autômato executando
        System.out.print("\nAutômato percorrendo a sentença" + "\n\n");
        String p = aut.getEstadoinicial().getName(); // Seleciona o estado inicial
        System.out.print(p);
        for (i = 0; i < sentenca.length(); i++) {
            // Encontra o próximo estado
            p = proximoEstado(p, sentenca.substring(i, i + 1));
            if(p == null)
                return false;
            System.out.print(" -> " + p); // Imprime o estado atual
        }

        if (estadoFinalAFD(p))
            return true;
        else 
           return false;       
    }

    public static void main(String args[]) throws IOException {
        Scanner arq = new Scanner(System.in);
        String ler;
        String sentenca;
        System.out.print("***************************");
        System.out.printf("\n\nConversor de AFN para AFD\n\n");
        System.out.print("***************************");

        System.out.printf("\nInforme o nome do arquivo do AFN de entrada: ");
        ler = arq.next();
        String path = "./Arquivo_Entrada/" + ler + ".jff";
        Path arquivo = Paths.get(path);

        while(Files.notExists(arquivo)){
            System.out.printf("\nArquivo não encontrado! Informe o nome do arquivo do AFN de entrada: ");
            ler = arq.next();
            path = "./Arquivo_Entrada/" + ler + ".jff";
            arquivo = Paths.get(path);
        }

        automato_afn = LerXml.readXml("./Arquivo_Entrada/" + ler + ".jff");
        System.out.printf("\nInforme a sentença: ");
        sentenca = arq.next();
        arq.close();
        
        String alfabetoo = automato_afn.getAlfabeto();
        alfabeto = alfabetoo.split(",");

        constroiEstados(automato_afn.getEstadoinicial().getName());

        GerarXML gera = new GerarXML();
        gera.gerar(auto_afd, ler + ".jff", true);

        boolean res = verificaSentenca(auto_afd,sentenca);
        String resultado;
        if(res == true)
            resultado = "Sentença aceita";
        else
            resultado = "Sentença rejeitada";

        System.out.printf("\n\nResultado: " + resultado);

        System.out.printf("\n\nArquivo gerado com AFD!\n\n");
    }
}