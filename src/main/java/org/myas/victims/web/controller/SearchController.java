package org.myas.victims.web.controller;

import java.util.List;

import org.myas.victims.core.domain.Victim;
import org.myas.victims.search.manager.ESSearchManager;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ESSearchManager esSearchManager;

    @RequestMapping(method = RequestMethod.GET)
    public List<Victim> search(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String village,
                               @RequestParam(required = false) String district) {
        // TODO: add error on all empty
        return esSearchManager.searchVictims(village, district, name);
    }
}
