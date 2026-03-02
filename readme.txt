Mecânicas do jogo:

    Controlo Duplo - O jogador alterna entre o Peixe Grande e o Peixe Pequeno, cada um com habilidades e restrições específicas.

    Interação com Objetos - Implementação de lógicas de colisão, empurrar objetos (Pushable), destruir troncos, rebentar bombas e interagir com inimigos (Caranguejos) e armadilhas.

    Física (Gravidade) - Sistema de gravidade implementado onde objetos não suportados caem, podendo esmagar outros objetos ou o jogador.

    Níveis - Carregamento dinâmico de mapas a partir de ficheiros de texto.

    Sistema de Pontuação e Persistência:

        Registo do nome do jogador no início da sessão.

        Contagem de tempo decorrido, número de movimentos e número de tentativas.

        ScoreBoard - Sistema de Highscores que guarda e lê os resultados num ficheiro de texto (tabelas.txt), apresentando o Top 10 dos melhores jogadores ordenados por tentativas, tempo e movimentos.

    Estrutura Técnica e Design:

        Hierarquia de Classes - Uso de herança e de polimorfismo. A classe base GameObject deriva em GameCharacter, FixedObject, e itens específicos (tentei ser o mais flexivel e escalavél com o meu código)

        Interfaces - Utilização de interfaces para definir comportamentos, como Pushable (objetos empurráveis), Gravity (objetos que caem), Enemy (inimigos) e SmallObject (interface de marcação para objetos que passam na HoledWall).

        Gestão de Tempo: Classe Time personalizada que converte os ticks do motor de jogo em segundos reais para uma contagem precisa.

Todas as funções e classes estão comentadas e explicadas sucintamente.