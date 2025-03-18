package com.example.mc_account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
@Data
@NoArgsConstructor
public class StorageBody {

    private Resource file;

}
