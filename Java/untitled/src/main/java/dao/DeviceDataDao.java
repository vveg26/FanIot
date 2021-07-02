package dao;

import entity.mvc.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author aeolus
 * @program IOT201851385129
 * @description
 * @date 2021-05-31 09:56:56
 */

@Repository
public class DeviceDataDao {
    private final MongoTemplate mongoTemplate;

    public DeviceDataDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    private Class getClass(String dataType) throws ClassNotFoundException {
        dataType = "entity.mvc." + dataType.substring(0, 1).toUpperCase() + dataType.substring(1);
        return Class.forName(dataType);
    }

    public void createCollection() {
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

    public void insert(Data data) {
        mongoTemplate.insert(data);
    }

    /**
     * @param skip  查询起始位置（从0开始）
     * @param limit 查询数据条数
     */
    public List findListByDeviceId(int deviceId, int skip, int limit) throws ClassNotFoundException {
        DeviceDao deviceDao = new DeviceDao(mongoTemplate);
        Device device = deviceDao.findOneByDeviceId(deviceId);
        Query query = new Query();
        query.addCriteria(new Criteria("deviceId").is(device.getId()));
        query.skip(skip).limit(limit);
        return mongoTemplate.find(query, getClass(device.getDataType()));
    }

    public List findListByDeviceName(String deviceName, int skip, int limit) throws ClassNotFoundException {
        Query query = new Query();
        query.addCriteria(new Criteria("name").is(deviceName));
        Device device = mongoTemplate.findOne(query, Device.class);
        Query query1 = new Query();
        if (device != null) {
            return findListByDeviceId(device.getId(), skip, limit);
        }
        return null;
    }

    /**
     * 根据deviceId删除所有数据
     */
    public boolean delete(int deviceId) throws ClassNotFoundException {
        DeviceDao deviceDao = new DeviceDao(mongoTemplate);
        Device device = deviceDao.findOneByDeviceId(deviceId);
        Query query = new Query();
        query.addCriteria(new Criteria("deviceId").is(deviceId));
        long affectedRow = mongoTemplate.remove(query, getClass(device.getDataType())).getDeletedCount();
        return affectedRow != 0;    }

    /**
     * 根据deviceName删除指定数据
     * @param key mongodb对应字段名
     * @param value String型数值
     */
    public boolean delete(String deviceName, String key, String value) throws ClassNotFoundException {
        DeviceDao deviceDao = new DeviceDao(mongoTemplate);
        Device device = deviceDao.findOneByDeviceName(deviceName);
        Query query = new Query();
        query.addCriteria(new Criteria(key).is(value));
        long affectedRow = mongoTemplate.remove(query, getClass(device.getDataType())).getDeletedCount();
        return affectedRow != 0;    }

    /**
     * 根据deviceName删除指定数据
     * @param key mongodb对应字段名
     * @param value int型数值
     */
    public boolean delete(String deviceName, String key, int value) throws ClassNotFoundException {
        DeviceDao deviceDao = new DeviceDao(mongoTemplate);
        Device device = deviceDao.findOneByDeviceName(deviceName);
        Query query = new Query();
        query.addCriteria(new Criteria(key).is(value));
        long affectedRow = mongoTemplate.remove(query, getClass(device.getDataType())).getDeletedCount();
        return affectedRow != 0;
    }


}
