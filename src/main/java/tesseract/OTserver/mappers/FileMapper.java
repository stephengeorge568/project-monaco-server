package tesseract.OTserver.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tesseract.OTserver.objects.CreateDocumentRequest;
import tesseract.OTserver.objects.GetDocumentResponse;

@Mapper
public interface FileMapper {

    GetDocumentResponse getDocumentById(@Param("id") Long id);

    Long createDocument(@Param("request") CreateDocumentRequest request);

}

