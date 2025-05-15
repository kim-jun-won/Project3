package Project3.LMS.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileDownloadController {
    @GetMapping("/materials/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("filename") String filename) {
        try {
            Path filePath = Paths.get("C:/uploads").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            String originalName = filename.substring(filename.indexOf('_') + 1);
            String encodedName = URLEncoder.encode(originalName, "UTF-8").replaceAll("\\+", "%20");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedName)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}

