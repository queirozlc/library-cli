package com.faesa.librarycli.shared.infra.shell;

import com.faesa.librarycli.core.checkoutbook.LoanRepository;
import com.faesa.librarycli.core.createauthor.AuthorRepository;
import com.faesa.librarycli.core.createbook.BookRepository;
import com.faesa.librarycli.core.newinstance.InstanceRepository;
import com.faesa.librarycli.core.placinghold.HoldRepository;
import com.faesa.librarycli.core.registerpatron.PatronRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultOutput {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final InstanceRepository instanceRepository;
    private final PatronRepository patronRepository;
    private final HoldRepository holdRepository;
    private final LoanRepository loanRepository;


    public String build() {
        // do a custom splash screen as a banner which counts the number of rows on each entity
        String authorInfo = "Criado por:\n" +
                "    Rafael Klein\n" +
                "    Giordan Garcia\n" +
                "    Lucas Queiroz";
        String courseInfo = "Disciplina: Banco de Dados - 2023/2\n" +
                "Professor: Howard Roatti";

//        return "####################################################################################\n" +
//                "#                                SISTEMA DE BIBLIOTECA                          #\n" +
//                "#                                                                               #\n" +
//                "# TOTAL DE REGISTROS INSERIDOS:                                                 #\n" +
//                "# 1- AUTORES: " + authorRepository.count() + "                                   #\n" +
//                "# 2- LIVROS: " + bookRepository.count() + "                                     #\n" +
//                "# 3- EXEMPLARES: " + instanceRepository.count() + "                               #\n" +
//                "# 4- USUÁRIOS: " + patronRepository.count() + "                                  #\n" +
//                "# 5- RESERVAS: " + holdRepository.count() + "                                    #\n" +
//                "# 6- EMPRÉSTIMOS: " + loanRepository.count() + "                                 #\n" +
//                "#                                                                               #\n" +
//                "#                                                                               #\n" +
//                "#   create-book: Cria um novo livro na biblioteca.                              #\n" +
//                "#   new-instance: Registra uma nova instância de um livro na biblioteca.        #\n" +
//                "#   create-author: Cria um novo autor.                                          #\n" +
//                "#                                                                               #\n" +
//                "# Comandos Integrados                                                           #\n" +
//                "#                                                                               #\n" +
//                "#   help: Exibe ajuda sobre os comandos disponíveis.                              #\n" +
//                "#   stacktrace: Exibe a pilha de erros completa do último erro.                  #\n" +
//                "#   clear: Limpa a tela do console.                                              #\n" +
//                "#   quit, exit: Sai do console.                                                  #\n" +
//                "#                                                                               #\n" +
//                "#                                                                               #\n" +
//                authorInfo + "#\n" +
//                courseInfo + "#\n" +
//                "####################################################################################";

        final var builder = new StringBuilder();

        builder.append("#########################################################################################\n");
        builder.append("#                                SISTEMA DE BIBLIOTECA                                  #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# TOTAL DE REGISTROS INSERIDOS:                                                         #\n");
        builder.append("# 1 - AUTORES: ").append(authorRepository.count()).append("                             \n");
        builder.append("# 2 - LIVROS: ").append(bookRepository.count()).append("                                \n");
        builder.append("# 3 - EXEMPLARES: ").append(instanceRepository.count()).append("                        \n");
        builder.append("# 4 - USUÁRIOS: ").append(patronRepository.count()).append("                            \n");
        builder.append("# 5 - RESERVAS: ").append(holdRepository.count()).append("                              \n");
        builder.append("# 6 - EMPRÉSTIMOS: ").append(loanRepository.count()).append("                           \n");
        builder.append("#                                                                                       #\n");
        builder.append("#                                                                                       #\n");
        builder.append("========================================================================================\n");
        builder.append("#                                PRINCIPAIS COMANDOS                                    #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# COMANDOS PARA LIVRO:                                                                  #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# - create-book: Cria um novo livro na biblioteca.                                      #\n");
        builder.append("# - new-instance: Registra um novo exemplar de um livro na biblioteca.                  #\n");
        builder.append("# - create-author: Cria um novo autor.                                                  #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# COMANDOS PARA USUÁRIO:                                                                #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# - create-patron: Registra um novo usuário na biblioteca.                              #\n");
        builder.append("# - return-book: Registra a devolução de um livro emprestado.                           #\n");
        builder.append("# - checkout-book: Registra o empréstimo de um livro.                                   #\n");
        builder.append("# - place-hold: Registra a reserva de um livro.                                         #\n");
        builder.append("# - cancel-hold: Cancela a reserva de um livro.                                         #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# COMANDOS PARA CONSULTA:                                                               #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# - list-books: Lista todos os livros da biblioteca.                                    #\n");
        builder.append("# - list-authors: Lista todos os autores da biblioteca.                                 #\n");
        builder.append("# - list-patrons: Lista todos os usuários da biblioteca.                                #\n");
        builder.append("# - list-holds: Lista todas as reservas da biblioteca.                                  #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# COMANDOS PARA RELATÓRIOS:                                                             #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# - books-held-per-author: Lista a quantidade de livros reservados agrupados por autor. #\n");
        builder.append("# - patron-holds: Lista todos os livros que o usuário possui reservas.                  #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# COMANDOS INTEGRADOS                                                                   #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# - help: Exibe ajuda sobre os comandos disponíveis.                                    #\n");
        builder.append("# - stacktrace: Exibe a pilha de erros completa do último erro.                         #\n");
        builder.append("# - clear: Limpa a tela do console.                                                     #\n");
        builder.append("# - quit, exit: Sai do console.                                                         #\n");
        builder.append("#                                                                                       #\n");
        builder.append("#                                                                                       #\n");
        builder.append("========================================================================================\n");
        builder.append("#                                     DICA                                              #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# Para mais informações sobre um comando, digite '<comando> -h'.                        #\n");
        builder.append("#                                                                                       #\n");
        builder.append("# Você pode utilizar auto-complete para os comandos apertando TAB.                      #\n");
        builder.append("#                                                                                       #\n");
        builder.append("#                                                                                       #\n");
        builder.append("========================================================================================\n");
        builder.append("# ").append("# ").append(authorInfo).append("                                                                     \n");
        builder.append("# ").append("# ").append(courseInfo).append("                                                                     \n");
        builder.append("####################################################################################");
        return builder.toString();
    }
}
