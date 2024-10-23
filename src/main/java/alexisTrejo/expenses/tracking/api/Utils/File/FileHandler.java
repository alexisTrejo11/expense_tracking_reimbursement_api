package alexisTrejo.expenses.tracking.api.Utils.File;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class FileHandler {

    @Value("${file.upload.dir}")
    private String fileUploadDir;

    // Define the maximum file size (e.g., 5MB)
    private static final long MAX_FILE_SIZE_MB = 5 * 1024 * 1024; // 5 MB

    // Define allowed file extensions
    private static final List<String> ALLOWED_FILE_EXTENSIONS = Arrays.asList("pdf", "png", "jpeg", "jpg");

    public Result<String> uploadAttachmentFile(ExpenseDTO expenseDTO, MultipartFile file) throws IOException {
        Result<Void> fileSizeResult = validateFileSize(file);
        if (!fileSizeResult.isSuccess()) {
            return Result.error(fileSizeResult.getErrorMessage());
        }

        Result<Void> fileExtension = validateFileExtension(file);
        if (!fileExtension.isSuccess()) {
            return Result.error(fileExtension.getErrorMessage());
        }

        String fileName = createAttachmentFileName(expenseDTO, file);
        return Result.success(uploadFile(file, fileName));
    }

    private String createAttachmentFileName(ExpenseDTO expenseDTO, MultipartFile file) {
        LocalDateTime now = LocalDateTime.now();
        long attachmentNumber = (long) expenseDTO.getAttachments().size() + 1;

        String fileExtension = getFileExtension(file);

        return String.format("expense_%d_%d-%02d-%02d_%02d%02d%02d.%s",
                expenseDTO.getId(),
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute(),
                now.getSecond(),
                fileExtension);
    }

    private String uploadFile(MultipartFile file, String newFilename) throws IOException {
        Path uploadDirPath = Paths.get(fileUploadDir);
        if (!Files.exists(uploadDirPath)) {
            Files.createDirectories(uploadDirPath);
        }

        Path copyLocation = uploadDirPath.resolve(newFilename);
        Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);

        return copyLocation.toString();
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }


    private Result<Void> validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE_MB) {
            return Result.error("File size exceeds the maximum limit of 5 MB.");
        }

        return Result.success();
    }

    private Result<Void> validateFileExtension(MultipartFile file) {
        String fileExtension = getFileExtension(file);
        if (!ALLOWED_FILE_EXTENSIONS.contains(fileExtension)) {
            return Result.error("Invalid file format. Only PDF, PNG, JPEG, and JPG are allowed.");
        }

        return Result.success();
    }
}
