package com.lermzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Administrator on 5/18/2016.
 */
@Controller
public class WoodBarnSolverController {

    private static Logger log = LoggerFactory.getLogger( WoodBarnSolverController.class );

    @RequestMapping("/")
    public String root(Model model){ return "index";}

    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping(value="/solve", method= RequestMethod.GET )
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody String[] solve(@RequestParam("rows") String rows, @RequestParam("length") int length) {
        log.info("rows:" + rows + " length: " + length);
        //solveWords(char[][] board, int neededLength)
        String[] solution = new String[1];
        solution[0] = "ok";
        return solution;
    }
}
