package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.Models.ExpenseAttachment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-23T12:58:49-0600",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.9.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class AttachmentMapperImpl implements AttachmentMapper {

    @Override
    public AttachmentDTO entityToDTO(ExpenseAttachment expenseAttachment) {
        if ( expenseAttachment == null ) {
            return null;
        }

        AttachmentDTO attachmentDTO = new AttachmentDTO();

        attachmentDTO.setId( expenseAttachment.getId() );
        attachmentDTO.setAttachmentUrl( expenseAttachment.getAttachmentUrl() );
        attachmentDTO.setUploadedAt( expenseAttachment.getUploadedAt() );

        return attachmentDTO;
    }
}
