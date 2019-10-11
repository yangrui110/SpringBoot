package com.yangframe.web.dao.mysql;

import com.yangframe.chat.entity.HistoryEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ChatDao {

    List<Map<String,Object>> getFriendNotReadNum(@Param("userLoginId") String userLoginId);

    List<Map<String,Object>> getGroupNotReadNum(@Param("groups") List<String> groups,@Param("userLoginId")String userLoginId);

    List<Map<String,Object>> getHistoryMsg(@Param("historyEntity")HistoryEntity historyEntity);
}
