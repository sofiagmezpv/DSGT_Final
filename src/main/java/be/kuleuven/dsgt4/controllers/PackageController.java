package be.kuleuven.dsgt4.controllers;

import be.kuleuven.dsgt4.services.FirestoreService;
import be.kuleuven.dsgt4.models.Package;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class PackageController {

    @Autowired
    private FirestoreService firestoreService;

    @GetMapping("/packages")
    public List<Package> getAllPackages() {
        return new ArrayList<>(firestoreService.fetchPackages().values());
    }

}
