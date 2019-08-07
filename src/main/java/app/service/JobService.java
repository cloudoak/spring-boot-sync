package app.service;

import app.enums.ElasticsearchIndexType;
import app.service.job.SyncJobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 *
 * Job service.
 *
 * @author OAK
 *
 */
@Service
public class JobService {

    private static Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    @Qualifier("costJob")
    SyncJobService syncJobService;

    @PostConstruct
    public void init(){
        syncJobService.run("cfe_pcg_mtmsalesorg_2", ElasticsearchIndexType.UPDATE, null);
    }

}
