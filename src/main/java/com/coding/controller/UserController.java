package com.coding.controller;

import com.coding.entity.User;
import com.coding.utils.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import com.coding.common.Const;
import com.coding.utils.PageResult;
import com.coding.utils.RequestPage;
import io.swagger.annotations.ApiOperation;
import com.coding.service.IUserService;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;

/**
* <p>
 * 用户表 前端控制器
 * </p>
*
* @author roma@guanweiming.com
* @since 2025-11-02
*/
@Slf4j
@Api(tags = "用户表")
@RequestMapping(Const.ADMIN + "user")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final IUserService userService;

    @ApiOperation("分页")
    @GetMapping("page")
    public PageResult<User> page(
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            @Validated RequestPage param) {
        return userService.page(role, status, param);
    }

    /**
    * 新增
    * @author roma@guanweiming.com
    * @date 2025/11/02
    **/
    @ApiOperation("新增")
    @PostMapping("add")
    public R<String> add(@RequestBody @Validated User user){
        return userService.add(user);
    }

    /**
    * 刪除
    * @author roma@guanweiming.com
    * @date 2025/11/02
    **/
    @ApiOperation("刪除")
    @GetMapping("delete")
    public R<String> delete(@RequestParam long id){
        return userService.delete(id);
    }

    /**
    * 更新
    * @author roma@guanweiming.com
    * @date 2025/11/02
    **/
    @ApiOperation("根据主键更新")
    @PostMapping("update")
    public R<String> update(@RequestBody @Valid User user){
        if (user.getUserId() == null) {
            return R.createByErrorMessage("传参有误，请检查");
        }
        return userService.update(user);
    }

    /**
    * 查询 根据主键 id 查询
    * @author roma@guanweiming.com
    * @date 2025/11/02
    **/
    @ApiOperation("查询 根据主键 id 查询")
    @GetMapping("detail")
    public R<User> findById(@ApiParam(name = "id", value = "主键id", required = true) @RequestParam long id){
        return userService.findById(id);
    }
}
