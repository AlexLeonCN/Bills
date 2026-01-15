package org.alex.bills.mapper;

import java.util.List;
import org.alex.bills.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    List<User> findAll();

    User findById(@Param("id") Long id);
}
