package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    //Constante para definicion de tamaño máximo permitido de archivos (5MB)
    private static final long MAX_FILE_SIZE = 5*1024*1024;

    //Constante para definicion de tipos de archivos permisibles
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    //Cliente de Cloudinary inyectado como dependencia
    private final Cloudinary cloudinary;

    //SE NECESITA CONSTRUCTOR PARA USAR VARIABLE
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Subir imagenes a la raíz de Cloudinary SIN CATEGORIZAR
     * @param file
     * @return String URL de la imagen
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException{
        //1. Validación de archivo
        validateImage(file);

        //Con el Map, se hace lo siguiente:
        //Subir el archivo a cloudinary con configuraciones básicas
        //Configura tipo de recurso auto-detectado
        //Calidad automática con nivel "good"
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto", "quality", "auto:good"
        ));

        return (String) uploadResult.get("secure_url");

        //2
    }

    /**
     * Subir imagenes a una carpeta en específico
     * @param file
     * @param folder carpeta destino
     * @return URL segura (HTTP) de la imagen subida
     * @throws IOException Si ocurre un error durante la subida
     */
    public String uploadImageFolder(MultipartFile file, String folder) throws IOException{
        //Validación de archivo
        validateImage(file);

        String originalFileName = file.getOriginalFilename(); //Generar un nombre único para el archivo
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); //Conservar la extensión
        String uniqueFileName = "img_" + UUID.randomUUID() + fileExtension; //Agregar un prefijo y UUID para evitar colisiones


        //Configuración para subir imagen
        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder, //Carpeta destino
                "public_id", uniqueFileName, //Nombre único de archivo
                "use_filename", false,  //No guardar el nombre original
                "unique_filename", false, //No generar nombre único (hecho anteriormente)
                "overwrite", false, // No sobreescritura
                "resource_type", "auto", //Autodetección de tipo de archivo
                "quality", "auto:good" //Optimización de calidad automática
        );

        //Subir el archivo
        Map<?,?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
    }

    /**
     *
     * @param file
     */
    private void validateImage(MultipartFile file){
        //1. Verificar si el archivo esta vacío
        if (file.isEmpty()){
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }

        //2. Validar el tamaño del archivo
        if (file.getSize() > MAX_FILE_SIZE){
            throw new IllegalArgumentException("El archivo no puede ser mayor a 5MB");
        }

        //3. Validar el nombre del archivo
        String originalFileName = file.getOriginalFilename();

        if(originalFileName == null){
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }

        //4. Extraer y validar la extensión
        String extension = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();

        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension)){
            throw new IllegalArgumentException("Extension inválida");
        }

        //5. Verifica que el tipo de MIME sea una imagen
        //Un MIME es el conjunto de metadatos detras de los archivos, los cuales permiten interpretarlos correctamente
        if (!file.getContentType().startsWith("image/")){
            throw new IllegalArgumentException("El archivo debe ser una imagen inválida");
        }




    }

}
