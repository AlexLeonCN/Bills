package org.alex.bills.mapper;

import java.util.List;
import org.alex.bills.model.Ledger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProjectMapper {
    int insert(Ledger project);

    int update(Ledger project);

    int deleteById(@Param("id") Long id);

    Ledger findById(@Param("id") Long id);

    List<Ledger> findAll();
}
