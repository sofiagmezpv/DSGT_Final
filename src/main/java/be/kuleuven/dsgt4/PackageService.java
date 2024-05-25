package be.kuleuven.dsgt4;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PackageService {

    @Autowired
    private FirestoreService firestoreService;

    public Package getPackageFromId(String id) {
        Map<String, Package> packages = firestoreService.fetchPackages();
        return packages.get(id);
    }
}