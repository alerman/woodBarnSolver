package wordbarn;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 5/18/2016.
 */
public class WordSolution implements Comparable {
    List<Vertex> vertexList = Lists.newArrayList();
    String word = "";
    List<WordSolution> otherWords = Lists.newArrayList();

    public WordSolution(String word, List<Vertex> vertices)
    {
        this.vertexList = vertices;
        this.word = word;
    }

    public List<Vertex> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int compareTo(Object o) {
        if(o instanceof WordSolution)
        {
            return word.compareTo(((WordSolution) o).getWord());
        }else return 1;
    }
}
