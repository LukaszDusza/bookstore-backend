package devlab.app.controller;


import devlab.app.model.MyFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/books/")
public class FilesController {

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

    @GetMapping("storage")
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

    @DeleteMapping("delete/{file}")
    public void delete(@PathVariable("file") String fileName) {

        File file = new File(UPLOADED_FOLDER + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam String filename, HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        response.setStatus(HttpServletResponse.SC_OK);

        InputStream is = new FileInputStream(filename);
        FileCopyUtils.copy(is, response.getOutputStream());
        response.flushBuffer();
    }

}
