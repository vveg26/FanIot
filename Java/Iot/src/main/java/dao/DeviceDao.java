package dao;

import entity.mvc.DataType;
import entity.mvc.Device;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description
 * @date 2021-05-31 00:13:11
 */

@Repository
public class DeviceDao {
    private final MongoTemplate mongoTemplate;

    public DeviceDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void createCollection() {
        if (!mongoTemplate.collectionExists(Device.class)) {
            mongoTemplate.createCollection(Device.class);
        }
        if (!mongoTemplate.collectionExists(DataType.ALERT)) {
            mongoTemplate.createCollection(DataType.ALERT);
        }
        if (!mongoTemplate.collectionExists(DataType.WAYPOINT)) {
            mongoTemplate.createCollection(DataType.WAYPOINT);
        }
        if (!mongoTemplate.collectionExists(DataType.MEASUREMENT)) {
            mongoTemplate.createCollection(DataType.MEASUREMENT);
        }
        if (!mongoTemplate.collectionExists(DataType.STATUS)) {
            mongoTemplate.createCollection(DataType.STATUS);
        }
    }

    public void insert(Device deviceEntity) {
        mongoTemplate.insert(deviceEntity);
    }

    /**
     * @param skip 查询起始位置（从0开始）
     * @param limit 查询数据条数
     */
    public List<Device> findList(int skip, int limit) {
        Query query = new Query();
        query.skip(skip).limit(limit);
        return mongoTemplate.find(query, Device.class);
    }

    public Device findOneByDeviceId(int deviceId) {
        return mongoTemplate.findById(deviceId, Device.class);
    }

    public Device findOneByDeviceName(String deviceName) {
        Query query = new Query(Criteria.where("name").is(deviceName));
        return mongoTemplate.findOne(query, Device.class);
    }

    public void update(Device deviceEntity) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(deviceEntity.getId()));
        Update update = new Update();
        updateIfNotNull(update, "dataType", deviceEntity.getDataType());
        updateIfNotNull(update, "latestUpdateTime", deviceEntity.getLatestUpdateTime());
        updateIfNotNull(update, "name", deviceEntity.getName());
        updateIfNotNull(update, "online", deviceEntity.getOnline());
        mongoTemplate.updateFirst(query, update, Device.class);
    }
    private void updateIfNotNull(Update update, String key, String value) {
        if (value != null && !value.isEmpty()) {
            update.set(key, value);
        }
    }
    private void updateIfNotNull(Update update, String key, int value) {
        if (value == 0 || value == 1) {
            update.set(key, value);
        }
    }

    public void delete(int deviceId) {
        Query query = new Query();
        query.addCriteria(new Criteria("_id").is(deviceId));
        mongoTemplate.remove(query, Device.class);
    }
}
