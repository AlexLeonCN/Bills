package org.alex.bills.mapper;

import java.util.List;
import org.alex.bills.model.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BillMapper {
    int insertBatch(@Param("bills") List<Bill> bills);
}
