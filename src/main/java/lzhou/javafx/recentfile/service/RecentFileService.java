package lzhou.javafx.recentfile.service;

import java.util.List;

import lzhou.javafx.recentfile.entity.RecentFile;

public interface RecentFileService {

	public RecentFile save(RecentFile entity);
	
	public void delete(RecentFile entity);
	
	public long count();
	
	public Iterable<RecentFile> findAllOrderByIdDesc();
}
