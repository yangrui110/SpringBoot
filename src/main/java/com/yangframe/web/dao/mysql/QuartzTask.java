package com.yangframe.web.dao.mysql;

import java.util.List;
import java.util.Map;

public interface QuartzTask {

    List<Map<String,Object>> findTaskLists();

}
