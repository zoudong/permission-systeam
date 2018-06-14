import com.Application;
import com.zoudong.permission.mapper.TestMapper;
import com.zoudong.permission.utils.EhcacheUtil;
import com.zoudong.permission.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zd
 * @description class
 * @date 2018/6/4 17:10
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private RedisUtils redisUtils;

    @org.junit.Test
    public void testMybatis() {
        com.zoudong.permission.model.Test test = new com.zoudong.permission.model.Test();
        test.setTest("test");
        testMapper.insert(test);
    }

    @org.junit.Test
    public void testRedis() {
        String key = "zoudong";
        //如果缓存存在
        boolean hasKey = redisUtils.exists(key);
        if (hasKey) {
            //获取缓存
            Object object = redisUtils.get(key);
            log.info("从缓存获取的数据" + object);
        } else {
            //从DB中获取信息
            log.info("从数据库中获取数据");
            List<com.zoudong.permission.model.Test> tests = testMapper.selectAll();
            //数据插入缓存（set中的参数含义：key值，user对象，缓存存在时间10（long类型），时间单位）
            redisUtils.set(key, tests, 10L, TimeUnit.SECONDS);
            log.info("数据插入缓存" + tests.toString());
        }

    }


    @org.junit.Test
    public void testEhcache() {
        String key = "zoudong";
        //如果缓存存在
        List<com.zoudong.permission.model.Test> tests = testMapper.selectAll();
        EhcacheUtil.getInstance().put("permission", key, tests);
        if (EhcacheUtil.getInstance().get("permission", key) != null) {
            Object value = EhcacheUtil.getInstance().get("permission", key);
            log.info("ehcache value:{}", value);
        }else{
            log.info("not fund ehcache value!");
        }


    }


}
