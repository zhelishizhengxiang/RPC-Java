package com.simon.rpc.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhengx
 * @version 1.0
 * @create 2025/10/7
 *  用户实体类
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    
    private Integer id;
    private String userName;
    /*true为男，false为女*/
    private boolean sex;
    

}