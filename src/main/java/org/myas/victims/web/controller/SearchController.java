package org.myas.victims.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.myas.victims.core.domain.Victim;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mykhailo Yashchuk on 23.05.2017.
 */
@RestController
@RequestMapping("/search")
@CrossOrigin("*")
public class SearchController {
    @RequestMapping(method = RequestMethod.GET)
    public List<Victim> search(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String village,
                               @RequestParam(required = false) String district) {
        System.out.println("search: " + name + " " + village + " " + district);

        List<Victim> result = new ArrayList<>();
        Victim a = new Victim();
        a.setName("Test Pest");
        a.setDistrict("Test Deistrict");
        a.setFullRecord("FDsdfsdfasdf");
        a.setVillage("asdasdasdasd");
        result.add(a);
        result.add(a);
        a.setName(a.getName() + new Random().nextInt());
        result.add(a);
        return result;
    }
}
