package wordbarn;

import com.google.common.collect.Lists;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 5/17/2016.
 */
public class Vertex {
    public ArrayList<Vertex> adjacents;
    Character vertexChar;
    Point position;

    public Vertex(Character character, Point position)
    {
        this.position = position;
        this.adjacents = Lists.newArrayList();
        this.vertexChar = character;
    }


}
