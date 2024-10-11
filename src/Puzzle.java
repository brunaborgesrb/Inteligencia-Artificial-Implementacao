import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Classe principal do quebra-cabeça
class Puzzle {
    private int[][] state; // Representação do estado do quebra-cabeça
    private int[] emptyPos; // Posição do espaço vazio

    // Construtor que inicializa o quebra-cabeça com um estado inicial
    public Puzzle(int[][] initialState) {
        this.state = initialState; // Define o estado inicial
        this.emptyPos = findEmptyPosition(); // Encontra a posição do espaço vazio
    }

    // Método que localiza a posição do espaço vazio (representado por 0)
    private int[] findEmptyPosition() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] == 0) { // Se o valor é 0, retorna a posição
                    return new int[]{i, j};
                }
            }
        }
        return null; // Se não encontrar, retorna null
    }

    // Método para mover um bloco para a posição vazia
    public boolean move(int newRow, int newCol) {
        int row = emptyPos[0];
        int col = emptyPos[1];
        if (isValidMove(newRow, newCol)) { // Verifica se o movimento é válido
            swap(row, col, newRow, newCol); // Realiza a troca
            return true; // Movimento bem-sucedido
        }
        return false; // Movimento inválido
    }

    // Verifica se o movimento é válido (se a nova posição é adjacente ao espaço vazio)
    private boolean isValidMove(int newRow, int newCol) {
        int row = emptyPos[0];
        int col = emptyPos[1];
        return (Math.abs(row - newRow) == 1 && col == newCol) || (Math.abs(col - newCol) == 1 && row == newRow);
    }

    // Método que troca a posição de dois elementos no estado
    private void swap(int row1, int col1, int row2, int col2) {
        int temp = state[row1][col1]; // Armazena temporariamente o valor
        state[row1][col1] = state[row2][col2]; // Troca os valores
        state[row2][col2] = temp;
        emptyPos[0] = row2; // Atualiza a posição do espaço vazio
        emptyPos[1] = col2;
    }

    // Método que desfaz o movimento
    public void undoMove(int newRow, int newCol) {
        int row = emptyPos[0];
        int col = emptyPos[1];
        swap(row, col, newRow, newCol); // Inverte a troca
    }

    // Método que copia o estado e realiza um movimento
    public Puzzle copyAndEditState(int newRow, int newCol) {
        int[][] newState = new int[3][3]; // Cria uma nova matriz para o estado
        for (int i = 0; i < 3; i++) {
            System.arraycopy(state[i], 0, newState[i], 0, 3); // Copia o estado atual
        }
        int row = emptyPos[0];
        int col = emptyPos[1];
        newState[row][col] = newState[newRow][newCol]; // Realiza a troca na nova matriz
        newState[newRow][newCol] = 0; // Atualiza a posição vazia
        return new Puzzle(newState); // Retorna um novo objeto Puzzle com o novo estado
    }

    // Verifica se o estado atual é o estado objetivo
    public boolean isGoal() {
        int[][] goalState = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 0}
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (state[i][j] != goalState[i][j]) { // Compara com o estado objetivo
                    return false; // Se não coincidir, retorna falso
                }
            }
        }
        return true; // Se todos coincidirem, retorna verdadeiro
    }

    // Gera todos os vizinhos (estados possíveis) a partir do estado atual
    public List<Puzzle> getNeighbors() {
        List<Puzzle> neighbors = new ArrayList<>(); // Lista para armazenar vizinhos
        int row = emptyPos[0], col = emptyPos[1];

        // Adiciona movimentos possíveis
        if (row > 0) neighbors.add(copyAndEditState(row - 1, col)); // Mover para cima
        if (row < 2) neighbors.add(copyAndEditState(row + 1, col)); // Mover para baixo
        if (col > 0) neighbors.add(copyAndEditState(row, col - 1)); // Mover para a esquerda
        if (col < 2) neighbors.add(copyAndEditState(row, col + 1)); // Mover para a direita

        return neighbors; // Retorna a lista de vizinhos
    }

    // Método principal para executar o programa
    public static void main(String[] args) {
        int[][] initialState = {
            {1, 2, 3},
            {4, 0, 5},
            {7, 8, 6}
        };

        Puzzle puzzle = new Puzzle(initialState); // Inicializa o quebra-cabeça
        System.out.println("Estado inicial:");
        puzzle.printState(); // Imprime o estado inicial

        long startTime, endTime; // Variáveis para medir o tempo de execução

        // Exemplo de movimento
        if (puzzle.move(1, 1)) { // Mover a posição (1,1) para (1,2)
            System.out.println("\nNovo estado após a movimentação:");
            puzzle.printState(); // Imprime o novo estado
        }

        // Desfazendo o movimento
        puzzle.undoMove(1, 1); // Desfaz a movimentação
        System.out.println("\nEstado após desfazer a movimentação:");
        puzzle.printState(); // Imprime o estado após desfazer

        // Usando a cópia e edição do estado
        Puzzle newPuzzle = puzzle.copyAndEditState(1, 1); // Mover a posição (1,1) para (1,2)
        System.out.println("\nNovo estado após a cópia e movimentação:");
        newPuzzle.printState(); // Imprime o novo estado após a cópia
        
        // Verificando o estado original após a cópia
        System.out.println("\nEstado original após a cópia:");
        puzzle.printState(); // Imprime o estado original

        // Teste com cópia de estado
        startTime = System.nanoTime(); // Inicia o timer
        iterativeDeepeningSearchUsingCopy(puzzle); // Chama a busca em profundidade com cópia
        endTime = System.nanoTime(); // Para o timer
        System.out.println("Tempo de execução usando cópia de estado: " + (endTime - startTime) + " nanosegundos");

        // Resetando o estado
        puzzle = new Puzzle(initialState); // Reinicializa o quebra-cabeça

        // Teste com modificação direta
        startTime = System.nanoTime(); // Inicia o timer
        iterativeDeepeningSearchUsingDirectModification(puzzle); // Chama a busca em profundidade com modificação direta
        endTime = System.nanoTime(); // Para o timer
        System.out.println("Tempo de execução usando modificação direta: " + (endTime - startTime) + " nanosegundos");
    }

    // Busca em profundidade com cópia de estado
    public static void iterativeDeepeningSearchUsingCopy(Puzzle puzzle) {
        int depth = 0; // Profundidade inicial
        while (true) { // Laço infinito até encontrar a solução
            Set<String> visitedStates = new HashSet<>(); // Conjunto para armazenar estados visitados
            if (depthLimitedSearchUsingCopy(puzzle, depth, visitedStates)) {
                System.out.println("Solução encontrada na profundidade: " + depth); // Solução encontrada
                return; // Retorna ao método principal
            }
            depth++; // Aumenta a profundidade
        }
    }

    // Busca em profundidade limitada com cópia de estado
    public static boolean depthLimitedSearchUsingCopy(Puzzle puzzle, int depth, Set<String> visitedStates) {
        if (puzzle.isGoal()) {
            return true; // Se o estado é o objetivo, retorna verdadeiro
        }
        if (depth == 0) {
            return false; // Se a profundidade é zero, retorna falso
        }

        String stateKey = puzzleStateToString(puzzle.state); // Obtém a representação do estado como string
        if (visitedStates.contains(stateKey)) {
            return false; // Se o estado já foi visitado, retorna falso
        }
        visitedStates.add(stateKey); // Marca o estado como visitado

        for (Puzzle neighbor : puzzle.getNeighbors()) { // Para cada vizinho
            if (depthLimitedSearchUsingCopy(neighbor, depth - 1, visitedStates)) { // Chama recursivamente
                return true; // Se a solução for encontrada, retorna verdadeiro
            }
        }
        return false; // Se nenhuma solução for encontrada, retorna falso
    }

    // Busca em profundidade com modificação direta
    public static void iterativeDeepeningSearchUsingDirectModification(Puzzle puzzle) {
        int depth = 0; // Profundidade inicial
        while (true) { // Laço infinito até encontrar a solução
            Set<String> visitedStates = new HashSet<>(); // Conjunto para armazenar estados visitados
            if (depthLimitedSearchUsingDirectModification(puzzle, depth, visitedStates)) {
                System.out.println("Solução encontrada na profundidade: " + depth); // Solução encontrada
                return; // Retorna ao método principal
            }
            depth++; // Aumenta a profundidade
        }
    }

    // Busca em profundidade limitada com modificação direta
    public static boolean depthLimitedSearchUsingDirectModification(Puzzle puzzle, int depth, Set<String> visitedStates) {
        if (puzzle.isGoal()) {
            return true; // Se o estado é o objetivo, retorna verdadeiro
        }
        if (depth == 0) {
            return false; // Se a profundidade é zero, retorna falso
        }

        String stateKey = puzzleStateToString(puzzle.state); // Obtém a representação do estado como string
        if (visitedStates.contains(stateKey)) {
            return false; // Se o estado já foi visitado, retorna falso
        }
        visitedStates.add(stateKey); // Marca o estado como visitado

        int row = puzzle.emptyPos[0];
        int col = puzzle.emptyPos[1];

        // Matriz com os possíveis movimentos
        int[][] moves = {
            {row - 1, col}, // Mover para cima
            {row + 1, col}, // Mover para baixo
            {row, col - 1}, // Mover para a esquerda
            {row, col + 1}  // Mover para a direita
        };

        for (int[] move : moves) { // Para cada movimento possível
            int newRow = move[0];
            int newCol = move[1];

            // Verifica se a nova posição está dentro dos limites
            if (newRow >= 0 && newRow < 3 && newCol >= 0 && newCol < 3) {
                if (puzzle.move(newRow, newCol)) { // Realiza o movimento
                    if (depthLimitedSearchUsingDirectModification(puzzle, depth - 1, visitedStates)) { // Chama recursivamente
                        return true; // Se a solução for encontrada, retorna verdadeiro
                    }
                    puzzle.undoMove(newRow, newCol); // Desfaz o movimento
                }
            }
        }
        return false; // Se nenhuma solução for encontrada, retorna falso
    }

    // Converte o estado do quebra-cabeça em uma string para identificação única
    private static String puzzleStateToString(int[][] state) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : state) {
            for (int value : row) {
                sb.append(value).append(","); // Adiciona cada valor à string
            }
        }
        return sb.toString(); // Retorna a string representando o estado
    }

    // Imprime o estado do quebra-cabeça
    public void printState() {
        for (int[] row : state) {
            for (int value : row) {
                System.out.print(value + " "); // Imprime cada valor
            }
            System.out.println(); // Nova linha após cada linha do quebra-cabeça
        }
    }
}