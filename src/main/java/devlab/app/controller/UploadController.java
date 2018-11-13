package devlab.app.controller;


import devlab.app.model.MyFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/files/")
public class UploadController {

//    private ResourceLoader resourceLoader;
//
//    public UploadController(ResourceLoader resourceLoader) {
//        this.resourceLoader = resourceLoader;
//    }

    private static String UPLOADED_FOLDER = new File("").getAbsolutePath() + "//uploads//";

    @PostMapping("upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") List<MultipartFile> file, Principal principal) {

        createDirectory();

        if (!file.isEmpty()) {

            file.forEach(f -> {
                //  byte[] bytes = new byte[0];
                try {

                    byte[] bytes = f.getBytes();
                    Path path = Paths.get(UPLOADED_FOLDER + f.getOriginalFilename());
                    Files.write(path, bytes);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return new ResponseEntity<>(HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

//    Resource[] loadResources(String pattern) throws IOException {
//        return ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
//    }

    @GetMapping("upload")
    public List<MyFile> getResources() {

        createDirectory();

        try {
            List<MyFile> files = Files.walk(Paths.get(UPLOADED_FOLDER))
                    .filter(Files::isRegularFile)
                    .map(f -> new MyFile(f.getFileName().toString(), f.toAbsolutePath().toString()))
                    .collect(Collectors.toList());

            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public void createDirectory() {
        Path path = Paths.get(UPLOADED_FOLDER);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }
    }

    @DeleteMapping("upload/{file}")
    public void delete(@PathVariable("file") String fileName) {

        File file = new File(UPLOADED_FOLDER + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

}
