package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-24T16:30:05-0600",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.9.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class NotificationMapperImpl implements NotificationMapper {

    @Override
    public Notification insertDtoToEntity(NotificationInsertDTO expenseInsertDTO) {
        if ( expenseInsertDTO == null ) {
            return null;
        }

        Notification.NotificationBuilder notification = Notification.builder();

        notification.message( expenseInsertDTO.getMessage() );
        notification.type( expenseInsertDTO.getType() );

        notification.createdAt( java.time.LocalDateTime.now() );

        return notification.build();
    }

    @Override
    public NotificationDTO entityToDTO(Notification notification) {
        if ( notification == null ) {
            return null;
        }

        NotificationDTO notificationDTO = new NotificationDTO();

        notificationDTO.setUserId( notificationUserId( notification ) );
        notificationDTO.setId( notification.getId() );
        notificationDTO.setType( notification.getType() );
        notificationDTO.setMessage( notification.getMessage() );
        notificationDTO.setRead( notification.getRead() );

        return notificationDTO;
    }

    private Long notificationUserId(Notification notification) {
        if ( notification == null ) {
            return null;
        }
        User user = notification.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
