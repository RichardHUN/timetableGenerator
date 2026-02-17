package hu.unideb.inf.timetableGenerator.service.user;

import hu.unideb.inf.timetableGenerator.service.dao.StandardUserInfoDAO;

public interface UserService {

    StandardUserInfoDAO getUserInfo(String email);

}
