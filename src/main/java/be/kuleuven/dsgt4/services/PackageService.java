package be.kuleuven.dsgt4.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import be.kuleuven.dsgt4.models.Package;
import be.kuleuven.dsgt4.repositories.FirestoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageService {


    @Autowired
    private FirestoreRepository firestoreRepository;

    public Package getPackageFromId(String id) {
        Map<String, Package> packages = firestoreRepository.fetchPackages();
        return packages.get(id);
    }

    public List<Package> getAllPackages() {
        return new ArrayList<>(firestoreRepository.fetchPackages().values());
    }

}
