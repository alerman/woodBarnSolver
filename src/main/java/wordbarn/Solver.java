package wordbarn;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Administrator on 5/17/2016.
 */
public class Solver {

    private Logger log = LoggerFactory.getLogger(Solver.class);

    public static void main(String[] l) throws IOException, ExecutionException, InterruptedException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter board:");
        String input = br.readLine();
        String[] boardString = input.split(" ");

        System.out.print("Enter Needed Lengths:");
        String lengthsString = br.readLine();
        String[] lengthsSplit = lengthsString.split(" ");
        int[] neededLengths = new int[lengthsSplit.length];
        for(int i = 0; i<lengthsSplit.length;i++)
        {
            neededLengths[i] = Integer.parseInt(lengthsSplit[i]);
        }

        System.out.print("Enter Known Words:");
        String known = br.readLine();

        Solver solver = new Solver();

        char[][] board = new char[boardString.length][boardString.length];
        for (int i = 0; i < boardString.length; i++)
            for (int j = 0; j < boardString.length; j++) {
                board[i][j] = boardString[i].charAt(j);
            }
        while(true) {
            List<WordSolution> matching = solver.solvePuzzle(board, neededLengths, known);
            Map<Integer, WordSolution> menuNumbers = Maps.newHashMap();
            int i = 1;
            System.out.println(matching.size());
            Set<String> seenWords = Sets.newHashSet();
            for (WordSolution solution : matching) {
                menuNumbers.put(i, solution);
                System.out.print(i + ": ");
                if (seenWords.add(solution.getWord())) {
                    System.out.print(solution.getWord() + " ");
                    for (Vertex v : solution.getVertexList()) {
                        System.out.print(v.vertexChar);
                        System.out.print("-");
                        System.out.print(v.position.x);
                        System.out.print(",");
                        System.out.print(v.position.y);

                        System.out.print("  ");
                    }
                    System.out.println();
                }

                i++;
            }

            int wordUsed = Integer.parseInt(br.readLine());
            WordSolution solution = menuNumbers.get(wordUsed);
            board = solver.removeVerticesFromBoard(board, solution, board.length);
            neededLengths = Arrays.copyOfRange(neededLengths,1,neededLengths.length);
        }
    }

    private List<WordSolution> solvePuzzle(char[][] board, int[] neededLength, String knownWord) throws IOException, ExecutionException, InterruptedException {

        List<WordSolution> wordsThatMatch = solveSingleWord(board, neededLength[0], knownWord);
        List<WordSolution> wordSolutionList = Lists.newArrayList(wordsThatMatch);

        for (int i = 0; i < wordSolutionList.size(); i++) {
            WordSolution current = wordSolutionList.get(i);
            if (!containsPossibleGraph(cloneArray(board), current, Arrays.copyOfRange(neededLength, 1, neededLength.length), wordsThatMatch)) {
                wordsThatMatch.remove(current);
            }
        }

        return wordsThatMatch;
    }

    private boolean containsPossibleGraph(char[][] boardEdited, WordSolution current, int[] neededLength, List<WordSolution> wordsThatMatch) throws ExecutionException, InterruptedException {
        boolean result = false;
        if (neededLength.length <= 1) {
            return true;
        }

        boardEdited = removeVerticesFromBoard(boardEdited, current, neededLength[0]);

        List<WordSolution> subWordsThatMatch = solveSingleWord(boardEdited, neededLength[0], null);
        if (subWordsThatMatch.size() == 0) {
            wordsThatMatch.remove(current);
        } else {
            if(neededLength.length >1) {
                int[] remainingLengths = Arrays.copyOfRange(neededLength, 1, neededLength.length);
                for (WordSolution solution : subWordsThatMatch) {
                    if (containsPossibleGraph(cloneArray(boardEdited), solution, remainingLengths, wordsThatMatch)) {
                        result = true;

                    }
                }
            }
        }

        return result;
    }


    private char[][] cloneArray(char[][] board) {
        char[][] boardEdited = new char[board.length][];
        for (int i = 0; i < board.length; i++) {
            boardEdited[i] = Arrays.copyOf(board[i], board[i].length);
        }
        return boardEdited;
    }

    private char[][] removeVerticesFromBoard(char[][] originalBoard, WordSolution current, int length) {
        char[][] result = cloneArray(originalBoard);
        for (Vertex v : current.getVertexList()) {
            result[v.position.x][v.position.y] = '1';
        }

        for(int i=0;i<length;i++) {
            dropTheLetters(originalBoard, result);
        }
        return result;
}

    private void dropTheLetters(char[][] originalBoard, char[][] result) {
        for (int j = originalBoard[0].length - 1; j >= 0; j--) {
            for (int i = originalBoard.length - 1; i >= 0; i--) {
                if (result[i][j] == '1') {
                    //Drop Letters!
                    int k = i - 1;
                    if (k >= 0) {
                        if (result[k][j] != '1') {
                            //I found a letter to drop. So keep dropping
                            char temp = result[i][j];
                            result[i][j] = result[k][j];
                            result[k][j] = temp;
                        }
                    }

                }
            }
        }
    }

    private List<WordSolution> solveSingleWord(char[][] board, int neededLength, String knownWord) throws ExecutionException, InterruptedException {
        Profiler profiler = new Profiler(getClass().getName());
        profiler.setLogger(log);


        profiler.start("Determine possible letters");
        Map<Character, Integer> allChars = getAllowedCharacters(board);
        log.debug("Possible Letters: " + allChars);


        profiler.start("Create possible word list");

        Set<String> allWords = loadDictionary(neededLength, allChars);
        if(StringUtils.isNoneBlank(knownWord))
        {
            allWords = Sets.newHashSet(knownWord);
        }
        profiler.stop();
        profiler.log();

        Map<Character, List<Vertex>> vertexMap = createGraph(board);

        return solveWord(allWords, vertexMap);
    }

    private static final ExecutorService workers = Executors.newCachedThreadPool();
    private List<WordSolution> solveWord(Set<String> allWords, final Map<Character, List<Vertex>> vertexMap) throws InterruptedException, ExecutionException {

        List<WordSolution> result = Lists.newArrayList();
        Collection<Callable<List<WordSolution>>> solutionFinders = new ArrayList<Callable<List<WordSolution>>>();
        List<List<String>> partitionedWords = Lists.partition(Lists.newArrayList(allWords), 50);
        for(final List<String> words : partitionedWords)
        {
            solutionFinders.add(new Callable<List<WordSolution>>() {
                public List<WordSolution> call() throws Exception {
                    return solveOneWord(words,vertexMap);
                }
            });
        }

        List<Future<List<WordSolution>>> results = workers.invokeAll(solutionFinders, 10, TimeUnit.MINUTES);
        for (Future<List<WordSolution>> f : results) {
            List<WordSolution> solutions = f.get();
            result.addAll(solutions);
        }
        return result;
    }

    private List<WordSolution> solveOneWord(Collection<String> allWords, Map<Character, List<Vertex>> vertexMap) {
        List<WordSolution> wordsThatMatch = Lists.newArrayList();
        for (String word : allWords) {
            //split by char
            //find all vertices with that char
            List<Vertex> verticesUsed = Lists.newArrayList();
            for (int i = 0; i < vertexMap.get(word.charAt(0)).size(); i++) {
                Vertex v = vertexMap.get(word.charAt(0)).get(i);
                verticesUsed.add(v);
                if (findPathFromVertex(v, vertexMap, word, verticesUsed)) {
                    if (verticesUsed.size() > word.length()) {
                        //subarray to get all the last letters
                        List<Vertex> common = verticesUsed.subList(0, word.length() - 1);
                        List<Vertex> lastLetter = verticesUsed.subList(word.length(), verticesUsed.size());
                        for (Vertex vert : lastLetter) {
                            List<Vertex> allUsed = Lists.newArrayList(common);
                            allUsed.add(vert);
                            wordsThatMatch.add(new WordSolution(word, allUsed));
                        }
                    } else {
                        wordsThatMatch.add(new WordSolution(word, Lists.<Vertex>newArrayList(verticesUsed)));
                        verticesUsed.clear();
                    }

                }
            }

        }
        return wordsThatMatch;
    }

    private Map<Character, List<Vertex>> createGraph(char[][] board) {
        Map<Character, List<Vertex>> vertexMap = Maps.newHashMap();
        //create vertices


        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++) {
                Vertex v = new Vertex(board[i][j], new Point(i, j));
                if (vertexMap.containsKey(board[i][j])) {
                    vertexMap.get(board[i][j]).add(v);
                } else {
                    vertexMap.put(board[i][j], Lists.<Vertex>newArrayList(v));
                }
            }
        return vertexMap;
    }

    private Set<String> loadDictionary(int neededLength, Map<Character, Integer> allChars) {
        Set<String> allWords = Sets.newTreeSet();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/Users/alerman/Google Drive/WordGueeser/wordbarnSolver/woodbarnSolver/src/main/resources/words.txt"));
            String line;
            whilepointer:
            while ((line = br.readLine()) != null) {
                // process the line.
                if (line.length() == neededLength) {
                    Map<Character, Integer> tempChars = new HashMap<Character, Integer>(allChars);
                    for (char curr : line.toCharArray()) {

                        Character currChar = new Character(curr);
                        if (!allChars.keySet().contains(currChar)) {
                            continue whilepointer;
                        } else {
                            if (tempChars.get(currChar) > 0) {
                                tempChars.put(currChar, tempChars.get(currChar) - 1);
                            } else {
                                continue whilepointer;
                            }
                        }
                    }

                    allWords.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR WAS " + e.getMessage());
        }
        return allWords;
    }

    private Map<Character, Integer> getAllowedCharacters(char[][] board) {
        List<Character> chars = Lists.newArrayList();
        {
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    chars.add(new Character(board[i][j]));
                }
            }
        }

        Map<Character, Integer> allChars = Maps.newHashMap();
        for (Character c : chars) {
            if (allChars.containsKey(c)) {
                allChars.put(c, allChars.get(c) + 1);
            } else {
                allChars.put(c, 1);
            }
        }
        return allChars;
    }

    private boolean findPathFromVertex(Vertex current, Map<Character, List<Vertex>> vertexMap, String word, List<Vertex> verticesUsed) {
        List<Vertex> usedToHere = Lists.newArrayList(verticesUsed);

        boolean result = false;

        int xPos = current.position.x;
        int yPos = current.position.y;
        if (word.length() == 0) {
            return true;
        }
        if (word.length() == 1) {
            return true;
        }

        //length>1:

        List<Vertex> verticesWithLetter = Lists.newArrayList(vertexMap.get(new Character(word.charAt(1))));
        verticesWithLetter.removeAll(verticesUsed);
        for (int i = 0; i < verticesWithLetter.size(); i++) {
            Vertex v = verticesWithLetter.get(i);
            int absX = Math.abs(xPos - v.position.x);
            int absY = Math.abs(yPos - v.position.y);
            if (absX <= 1 && absY <= 1 && !(absY == 0 && absX == 0)) {
                verticesUsed.add(v);
                if(findPathFromVertex(v, vertexMap, word.substring(1), verticesUsed))
                {
                    result = true;
                }

            }
        }

        if (result == false) {
            verticesUsed.remove(current);
        }
        return result;
    }

}
