import com.Application;
import com.zoudong.permission.mapper.TestMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zd
 * @description class
 * @date 2018/6/4 17:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {
    @Autowired
    private TestMapper testMapper;
    @org.junit.Test
    public void test(){
        com.zoudong.permission.model.Test test=new com.zoudong.permission.model.Test();
        test.setTestTest("test");
        testMapper.insert(test);
    }
}
