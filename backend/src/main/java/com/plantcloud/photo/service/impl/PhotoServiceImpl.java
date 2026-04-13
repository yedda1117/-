package com.plantcloud.photo.service.impl;

import com.plantcloud.photo.service.PhotoService;
import com.plantcloud.photo.vo.PhotoLogVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Override
    public PhotoLogVO upload(Long plantId, MultipartFile file, Long userId) {
        return PhotoLogVO.builder()
                .id(0L)
                .date(LocalDate.now().toString())
                .aiStatus("PENDING")
                .build();
    }

    @Override
    public List<PhotoLogVO> list(Long plantId) {
        return Collections.emptyList();
    }

    @Override
    public PhotoLogVO getByDate(Long plantId, LocalDate date) {
        return PhotoLogVO.builder().date(date.toString()).build();
    }
}
