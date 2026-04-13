package com.plantcloud.photo.service;

import com.plantcloud.photo.vo.PhotoLogVO;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface PhotoService {

    PhotoLogVO upload(Long plantId, MultipartFile file, Long userId);

    List<PhotoLogVO> list(Long plantId);

    PhotoLogVO getByDate(Long plantId, LocalDate date);
}
