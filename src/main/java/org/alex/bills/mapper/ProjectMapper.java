package org.alex.bills.mapper;

import java.util.List;
import org.alex.bills.model.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectMapper {
    int insert(Project project);

    int update(Project project);

    int deleteById(@Param("id") Long id);

    Project findById(@Param("id") Long id);

    List<Project> findAll();
}
