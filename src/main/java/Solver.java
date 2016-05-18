import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 5/17/2016.
 */
public class Solver {

    private Logger log = LoggerFactory.getLogger(Solver.class);

    public static void main(String[] args) throws IOException {
        Solver solver = new Solver();

        char[][] board = new char[args.length][args.length];
        for(int i = 0;i<args.length;i++)
            for(int j=0;j<args.length;j++)
            {
                board[i][j] = args[i].charAt(j);
            }
        Set<String> matching = solver.solvePuzzle(board, 7);
        System.out.println(matching.size());

        for(String word : matching)
        {
               System.out.println(word);
        }
    }

    private Set<String> solvePuzzle(char[][] board, int neededLength) throws IOException {
        Profiler profiler = new Profiler(getClass().getName());
        profiler.setLogger(log);


        profiler.start("Determine possible letters");
        Map<Character, Integer> allChars = getAllowedCharacters(board);
        log.debug("Possible Letters: " + allChars);


        profiler.start("Create possible word list");

        Set<String> allWords = loadDictionary(neededLength, allChars);

        profiler.stop();
        profiler.log();

        Map<Character, List<Vertex>> vertexMap = createGraph(board);

        Set<String> wordsThatMatch = solveWord(allWords, vertexMap);

        return wordsThatMatch;
    }

    private Set<String> solveWord(Set<String> allWords, Map<Character, List<Vertex>> vertexMap) {
        Set<String> wordsThatMatch = Sets.newTreeSet();
        for(String word : allWords)
        {
            //split by char
            //find all vertices with that char
            List<Vertex> verticesUsed = Lists.newArrayList();
            for(int i=0; i<vertexMap.get(word.charAt(0)).size();i++)
            {
                Vertex v = vertexMap.get(word.charAt(0)).get(i);
                verticesUsed.add(v);
                if(findPathFromVertex(v, vertexMap, word,verticesUsed))
                {
                    wordsThatMatch.add(word);

                }
            }

        }
        return wordsThatMatch;
    }

    private Map<Character, List<Vertex>> createGraph(char[][] board) {
        Map<Character, List<Vertex>> vertexMap = Maps.newHashMap();
        //create vertices


        for(int i = 0; i<board.length;i++)
            for(int j=0;j<board[i].length;j++)
            {
                Vertex v = new Vertex(board[i][j],new Point(i,j));
                if(vertexMap.containsKey(board[i][j]))
                {
                    vertexMap.get(board[i][j]).add(v);
                }else
                {
                    vertexMap.put(board[i][j], Lists.<Vertex>newArrayList(v));
                }
            }
        return vertexMap;
    }

    private Set<String> loadDictionary(int neededLength, Map<Character, Integer> allChars) {
        Set<String> allWords = Sets.newTreeSet();
        try {
            BufferedReader br = new BufferedReader(new FileReader("/Users/alerman/Downloads/WordGueeser/wordbarnSolver/woodbarnSolber/src/main/resources/words.txt"));
            String line;
            whilepointer:
            while ((line = br.readLine()) != null) {
                // process the line.
                if (line.length() == neededLength) {
                    Map<Character, Integer> tempChars = new HashMap<Character, Integer>(allChars);
                    for(char curr : line.toCharArray())
                    {

                        Character currChar = new Character(curr);
                        if(!allChars.keySet().contains(currChar))
                        {
                            continue whilepointer;
                        }else
                        {
                            if(tempChars.get(currChar) >0)
                            {
                                tempChars.put(currChar, tempChars.get(currChar)-1);
                            }else{
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
            if(allChars.containsKey(c))
            {
                allChars.put(c, allChars.get(c) + 1);
            }
            else
            {
                allChars.put(c,1);
            }
        }
        return allChars;
    }

    private boolean findPathFromVertex(Vertex current, Map<Character, List<Vertex>> vertexMap, String word, List<Vertex> verticesUsed)
    {

        boolean result = false;

        int xPos = current.position.x;
        int yPos = current.position.y;
        if(word.length() == 0)
        {
            return true;
        }
        if(word.length() == 1)
        {
            return true;
        }

        //length>1:

        List<Vertex> verticesWithLetter = Lists.newArrayList(vertexMap.get(new Character(word.charAt(1))));
         verticesWithLetter.removeAll(verticesUsed);
        for(int i=0; i<verticesWithLetter.size();i++)
        {
            Vertex v = verticesWithLetter.get(i);
            int absX = Math.abs(xPos - v.position.x);
            int absY = Math.abs(yPos-v.position.y);
            if(absX <= 1 && absY<=1 && !(absY == 0 && absX==0)  )
            {
                verticesUsed.add(v);
                result =  findPathFromVertex(v,vertexMap,word.substring(1), verticesUsed);

            }
        }

        if(result==false)
        {
            verticesUsed.clear();
        }
        return result;
    }

}
