package IntegracionBackFront.backfront.Controller.Cloudinary;

import IntegracionBackFront.backfront.Config.Cloudinary.CloudinaryConfig;
import IntegracionBackFront.backfront.Services.Cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/image")
@CrossOrigin
public class ImageController {

    @Autowired
    private final CloudinaryService cService;

    public ImageController(CloudinaryService cloudinaryService){
        this.cService = cloudinaryService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image")MultipartFile file) throws IOException {
        try{
            String imageUrl = cService.uploadImage(file);
            return ResponseEntity.ok(Map.of(
                    "message", "Imagen subida correctamente",
                    "url", imageUrl
            ));
        }catch(IOException ex){
            return ResponseEntity.internalServerError().body("Error al subir la imagen");
        }
    }


}
