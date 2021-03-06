package com.example.controller;

import com.example.rest.entity.Resources;
import com.example.rest.repo.ResourcesRepo;
import com.example.token.SecurityHelper;
import com.example.token.TokenPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author
 * @create 2021 - 03 - 03 上午 11:55
 **/
@RestController
public class PDFController {

    @Value("${aes.company}")
    private String company;

    @Autowired
    private ResourcesRepo resourcesRepo;

    @GetMapping("test")
    public String test(){
        return "testtest";
    }

    @Autowired
    private SecurityHelper securityHelper;

    // TODO 之後要刪除
    @PostMapping(value = "/PdfService/pdf/file/download")
    public ResponseEntity<byte[]> get(HttpServletRequest req, @RequestBody Map<String, Object> pdfObj)
            throws Exception {

        String fileName = (String) pdfObj.get("fileName");
        int fileCount = (int) pdfObj.get("fileCount");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Content-disposition", getFileName(fileName, "pdf"));
        Resources resources = resourcesRepo.findTopByFileName(fileName).orElseThrow(() -> new Exception("找不到該檔案"));
        return new ResponseEntity<>(resources.getContents(), headers, HttpStatus.OK);
    }


    @PostMapping(value = "/PdfService/pdf/temp/delete")
    public ResponseEntity<String> delete(HttpServletRequest req, @RequestBody Map<String, Object> pdfObj) {
        String fileName = (String) pdfObj.get("fileName");
        return new ResponseEntity<>("",
                HttpStatus.OK);
    }

    @GetMapping("aes/token")
    public String getCipherText() throws JsonProcessingException {
        return "Bearer " + securityHelper.encrypt(TokenPayload.toJson(company));
    }


    @GetMapping("aes/encrypt/{plainText}")
    public String encrypt(@PathVariable String plainText){
        return securityHelper.encrypt(plainText);
    }
    @GetMapping("aes/decrypt/{cipherText}")
    public String decrypt(@PathVariable String cipherText){
        return securityHelper.decrypt(cipherText);
    }


    /**
     * getFileName 檔案名稱
     *
     * @param
     * @return
     * @throws
     */
    private String getFileName(String path, String extension) throws UnsupportedEncodingException {

        String fileName = java.net.URLEncoder.encode(path, "UTF-8");

        StringBuilder sb = new StringBuilder();

        sb.append("inline; filename=\"");

        sb.append(fileName);

        sb.append("\"");

        return sb.toString();
    }
}
