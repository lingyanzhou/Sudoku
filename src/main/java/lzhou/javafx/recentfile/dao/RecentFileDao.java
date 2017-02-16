package lzhou.javafx.recentfile.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import lzhou.javafx.recentfile.entity.RecentFile;

public interface RecentFileDao extends Repository<RecentFile, Integer> {
	@Transactional
	public RecentFile save(RecentFile entity);
	@Transactional
	public long count();
	@Transactional
	public Iterable<RecentFile> findAll();
	@Transactional
	public Iterable<RecentFile> findAll(Sort sort);
	@Transactional
	public void delete(RecentFile entity);
}
