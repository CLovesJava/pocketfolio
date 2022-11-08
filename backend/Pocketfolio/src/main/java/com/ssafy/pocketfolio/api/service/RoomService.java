package com.ssafy.pocketfolio.api.service;

import com.ssafy.pocketfolio.api.dto.request.RoomReq;
import com.ssafy.pocketfolio.api.dto.response.RoomDetailRes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RoomService {
    /**
     * 마이룸 생성
     * @param userSeq 생성 회원 번호
     * @param req 방 정보
     * @return 생성된 방 번호
     * @throws IOException 썸네일 이미지 저장 실패 시 던져짐
     */
    long insertRoom(long userSeq, RoomReq req, MultipartFile thumbnail) throws IOException;

    /**
     * 마이룸 목록 조회
     * @param userSeq 본인(방 주인) 회원 번호
     * @return 마이룸 목록(포트폴리오 목록, 마이룸 정보: 썸네일, 제목, 좋아요 수, 조회수)
     */
    List<RoomDetailRes> findRoomList(long userSeq);

    /**
     * 방 조회
     * @param userSeq 방문자 회원 번호
     * @param roomSeq 방 번호
     * @return 마이룸(포트폴리오 목록, 마이룸 정보: 썸네일, 제목, 좋아요 수, 조회수), 방 주인 이름
     */
    RoomDetailRes findRoom(long userSeq, long roomSeq);

    /**
     * 마이룸 수정
     * @param req 방 정보
     * @param thumbnail 썸네일 이미지
     * @return 수정된 방 번호
     * @throws IOException 썸네일 이미지 저장 실패 시 던져짐
     */
    long updateRoom(long roomSeq, RoomReq req, MultipartFile thumbnail) throws IOException;

    /**
     * 마이룸 삭제
     * @param roomSeq 방 번호
     */
    void deleteRoom(long roomSeq);

    /**
     * 방 좋아요
     * @param userSeq 좋아요 누르는 회원의 번호 (현재 접속한 회원)
     * @param roomSeq 방 번호
     * @return 좋아요 여부
     */
    boolean insertRoomLike(long userSeq, long roomSeq);

    /**
     * 방 좋아요 취소
     * @param userSeq 좋아요 취소하는 회원의 번호 (현재 접속한 회원)
     * @param roomSeq 방 번호
     * @return 좋아요 취소 여부
     */
    boolean deleteRoomLike(long userSeq, long roomSeq);

    /**
     * 본인이 좋아요 남긴 방 목록
     * @param userSeq 회원 번호 (현재 접속한 회원)
     * @return 해당 방 목록
     */
    List<RoomDetailRes> findRoomLikeList(long userSeq);

    /**
     * 베스트 방 목록
     * @return 좋아요 내림차순 방 목록
     */
    List<RoomDetailRes> findRoomBestList();
}