package za.co.entelect.pojo.cis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Blob;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class Document {

    private Long documentId;
    private Blob document;

}