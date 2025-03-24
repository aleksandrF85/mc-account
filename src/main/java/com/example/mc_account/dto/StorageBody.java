package com.example.mc_account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class StorageBody {

    private MultipartFile file;

}
