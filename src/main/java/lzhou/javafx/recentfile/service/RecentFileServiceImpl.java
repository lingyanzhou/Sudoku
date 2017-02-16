package lzhou.javafx.recentfile.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lzhou.javafx.recentfile.dao.RecentFileDao;
import lzhou.javafx.recentfile.entity.RecentFile;

@Service
public class RecentFileServiceImpl implements RecentFileService {
	@Autowired
	RecentFileDao dao;
	
	public RecentFile save(RecentFile entity) {
		return dao.save(entity);
	}
	
	public void delete(RecentFile entity) {
		dao.delete(entity);
	}
	
	public long count() {
		return dao.count();
	}
	
	public Iterable<RecentFile> findAllOrderByIdDesc() {
		return dao.findAll(new Sort(Sort.Direction.DESC, "id"));
	}
}
