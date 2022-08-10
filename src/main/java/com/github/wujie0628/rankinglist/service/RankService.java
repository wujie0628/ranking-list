package com.github.wujie0628.rankinglist.service;

import java.util.Set;

/**
 * @Author jiewu19
 * @Date 2022/8/8
 * @Description
 **/
public interface RankService {

    void putValue(String value, int delta);

    void putValue(String value);

    Set getRankList(int start, int end );
}
