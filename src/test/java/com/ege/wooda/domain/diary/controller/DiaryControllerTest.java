package com.ege.wooda.domain.diary.controller;

import com.ege.wooda.domain.diary.domain.Diary;
import com.ege.wooda.domain.diary.dto.request.DiaryCreateRequest;
import com.ege.wooda.domain.diary.dto.request.DiaryUpdateRequest;
import com.ege.wooda.domain.diary.service.DiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ege.wooda.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.ege.wooda.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(DiaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
public class DiaryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiaryService diaryService;

    @Test
    @DisplayName("Diary를 생성한다.")
    public void add() throws Exception {
        List<String> urls = new ArrayList<String>();
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        List<MockMultipartFile> mockImgs = getMultipartFiles();

        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .memberId(1L)
                .title("Diary title")
                .subTitle("Diary subtitle")
                .memoryDate("2023-04-09")
                .place("Diary place")
                .contents("Diary contents")
                .imgUrls(urls)
                .build();
        String request = objectMapper.writeValueAsString(diaryCreateRequest);

        given(diaryService.save(anyList(), any()))
                .willReturn(1L);

        ResultActions result = this.mockMvc.perform(
                multipart("/api/v0/diaries")
                        .file(mockImgs.get(0))
                        .file(mockImgs.get(1))
                        .file(new MockMultipartFile("diaryCreateRequest", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                        .contentType("multipart/form-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("diary-add",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        //requestPartBody("images"),
                        requestPartFields("diaryCreateRequest",
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("Member Id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("title"),
                                fieldWithPath("subTitle").type(JsonFieldType.STRING).description("subTitle"),
                                fieldWithPath("memoryDate").type(JsonFieldType.STRING).description("memoryDate"),
                                fieldWithPath("place").type(JsonFieldType.STRING).description("place"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("contents"),
                                fieldWithPath("imgUrls").type(JsonFieldType.ARRAY).description("imgUrls")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("Diary Id")
                        )));
    }

    @Test
    @DisplayName("모든 diary를 조회한다.")
    public void getAll() throws Exception{
        List<Diary> diaryList=getDiaryList();
        List<Long> mockIdList=new ArrayList<>(Arrays.asList(1L,2L,3L));
        List<String> mockAuditList=new ArrayList<>(Arrays.asList("2023-05-15T17:13:06","2023-05-20T09:34:21","2023-05-31T23:11:42","2023-06-02T06:23:41"));
        //Diary Id
        ReflectionTestUtils.setField(diaryList.get(0),"id",mockIdList.get(0));
        ReflectionTestUtils.setField(diaryList.get(1),"id",mockIdList.get(1));
        ReflectionTestUtils.setField(diaryList.get(2),"id",mockIdList.get(2));
        //AuditEntity
        ReflectionTestUtils.setField(diaryList.get(0).getAuditEntity(),"createdAt",getLocalDateTime(mockAuditList.get(0)));
        ReflectionTestUtils.setField(diaryList.get(0).getAuditEntity(),"updatedAt",getLocalDateTime(mockAuditList.get(0)));
        ReflectionTestUtils.setField(diaryList.get(1).getAuditEntity(),"createdAt",getLocalDateTime(mockAuditList.get(1)));
        ReflectionTestUtils.setField(diaryList.get(1).getAuditEntity(),"updatedAt",getLocalDateTime(mockAuditList.get(2)));
        ReflectionTestUtils.setField(diaryList.get(2).getAuditEntity(),"createdAt",getLocalDateTime(mockAuditList.get(3)));
        ReflectionTestUtils.setField(diaryList.get(2).getAuditEntity(),"updatedAt",getLocalDateTime(mockAuditList.get(3)));

        given(diaryService.findDiaries())
                .willReturn(diaryList);

        ResultActions result = this.mockMvc.perform(
                get("/api/v0/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("diary-getAll",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("Diary Id"),
                                fieldWithPath("data[].memberId").type(JsonFieldType.NUMBER).description("Member Id"),
                                fieldWithPath("data[].title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data[].subTitle").type(JsonFieldType.STRING).description("소제목"),
                                fieldWithPath("data[].memoryDate").type(JsonFieldType.STRING).description("데이트 날짜"),
                                fieldWithPath("data[].place").type(JsonFieldType.STRING).description("장소"),
                                fieldWithPath("data[].contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("data[].imgUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                fieldWithPath("data[].auditEntity").type(JsonFieldType.OBJECT).description("생성된 시간"),
                                fieldWithPath("data[].auditEntity.createdAt").type(JsonFieldType.STRING).description("생성된 시간"),
                                fieldWithPath("data[].auditEntity.updatedAt").type(JsonFieldType.STRING).description("수정된 시간")
                                )));
    }

    @Test
    @DisplayName("해당 diary Id에 해당하는 Diary를 조회한다.")
    public void findById() throws Exception {
        List<String> urls = new ArrayList<String>();
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        Diary mockDiary = getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-05-30"), "place2", "contents2", urls);
        Long mockId = 2L;

        ReflectionTestUtils.setField(mockDiary,"id",mockId);
        ReflectionTestUtils.setField(mockDiary.getAuditEntity(),"createdAt",getLocalDateTime("2023-06-01T19:02:14"));
        ReflectionTestUtils.setField(mockDiary.getAuditEntity(),"updatedAt",getLocalDateTime("2023-06-01T19:02:14"));

        given(diaryService.findOne(anyLong()))
                .willReturn(mockDiary);

        ResultActions result = this.mockMvc.perform(
                get("/api/v0/diaries/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"));

        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("diary-find",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("다이어리 Id")),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("Diary Id"),
                                fieldWithPath("data.memberId").type(JsonFieldType.NUMBER).description("Member Id"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("data.subTitle").type(JsonFieldType.STRING).description("소제목"),
                                fieldWithPath("data.memoryDate").type(JsonFieldType.STRING).description("데이트 날짜"),
                                fieldWithPath("data.place").type(JsonFieldType.STRING).description("장소"),
                                fieldWithPath("data.contents").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("data.imgUrls").type(JsonFieldType.ARRAY).description("이미지 URL 목록"),
                                fieldWithPath("data.auditEntity.createdAt").type(JsonFieldType.STRING).description("생성된 시간"),
                                fieldWithPath("data.auditEntity.updatedAt").type(JsonFieldType.STRING).description("수정된 시간")
                        )));
    }

    @Test
    @DisplayName("해당 id의 Diary 정보를 수정한다.")
    public void modify() throws Exception {
        List<String> urls = new ArrayList<String>();
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        List<MockMultipartFile> mockImgs = getMultipartFiles();

        DiaryUpdateRequest diaryUpdateRequest = DiaryUpdateRequest.builder()
                .memberId(1L)
                .title("Diary title")
                .subTitle("Diary subtitle")
                .memoryDate("2023-04-09")
                .place("Diary place")
                .contents("Diary contents")
                .imgUrls(urls)
                .build();
        String request = objectMapper.writeValueAsString(diaryUpdateRequest);

        Diary mockDiary = getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-05-30"), "place2", "contents2", urls);
        Long mockId=2L;

        ReflectionTestUtils.setField(mockDiary,"id",mockId);

        given(diaryService.findOne(2L))
                .willReturn(mockDiary);
        given(diaryService.update(anyLong(), anyList(), any()))
                .willReturn(2L);

        ResultActions result = this.mockMvc.perform(
                multipart("/api/v0/diaries/{id}",mockId)
                        .file(mockImgs.get(0))
                        .file(mockImgs.get(1))
                        .file(new MockMultipartFile("diaryUpdateRequest", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                        .with(req->{
                            req.setMethod("PUT");
                            return req;
                        })
                        .contentType("multipart/form-data")
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        );

        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("diary-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(parameterWithName("id").description("다이어리 Id")),
                        requestPartFields("diaryUpdateRequest",
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("Member Id"),
                                fieldWithPath("title").type(JsonFieldType.STRING).description("title"),
                                fieldWithPath("subTitle").type(JsonFieldType.STRING).description("subTitle"),
                                fieldWithPath("memoryDate").type(JsonFieldType.STRING).description("memoryDate"),
                                fieldWithPath("place").type(JsonFieldType.STRING).description("place"),
                                fieldWithPath("contents").type(JsonFieldType.STRING).description("contents"),
                                fieldWithPath("imgUrls").type(JsonFieldType.ARRAY).description("imgUrls")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").type(JsonFieldType.NUMBER).description("Diary Id")
                        )));
    }

    @Test
    @DisplayName("특정 다이어리를 삭제한다.")
    public void remove() throws Exception{
        List<String> urls = new ArrayList<String>();
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        Diary mockDiary = getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-05-30"), "place2", "contents2", urls);
        Long mockId = 2L;

        given(diaryService.findOne(anyLong()))
                .willReturn(mockDiary);

        ResultActions result = this.mockMvc.perform(
                delete("/api/v0/diaries/{id}", mockId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andDo(document("diary-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("Diary Id")
                        ),
                        responseFields(
                                fieldWithPath("status").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("data").description("NULL")
                        )));
    }

    private Diary getDiary(Long memberId, String title, String subTitle, LocalDate memoryDate, String place, String contents, List imgUrls) {
        return Diary.builder()
                .memberId(memberId)
                .title(title)
                .subTitle(subTitle)
                .memoryDate(memoryDate)
                .place(place)
                .contents(contents)
                .imgUrls(imgUrls)
                .build();
    }


    private List<Diary> getDiaryList(){
        List<String> urls1 = new ArrayList<String>();
        urls1.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-1.png");
        urls1.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/홍길동/1-2.png");

        List<String> urls2 = new ArrayList<String>();
        urls2.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/여혜민/2-1.png");
        urls2.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/여혜민/2-2.png");
        urls2.add("https://s3.console.aws.amazon.com/s3/object/test?region=ap-northeast-2&amp;prefix=member/여혜민/2-3.png");

        return List.of(getDiary(1L, "Test Diary1", "Test diary subtitle1", getLocalDate("2023-06-01"), "place1", "contents1", urls1)
                ,getDiary(2L, "Test Diary2", "Test diary subtitle2", getLocalDate("2023-06-01"), "place2", "contents2", urls2)
                ,getDiary(3L, "Test Diary3", "Test diary subtitle3", getLocalDate("2023-06-01"), "place3", "contents3", urls2));
    }

    private List<MockMultipartFile> getMultipartFiles() {
        String path1 = "1-1.png";
        String path2 = "1-2.png";

        return List.of(new MockMultipartFile("1-1", path1, "image/png", "1-1".getBytes()), new MockMultipartFile("1-2", path2, "image/png", "1-2".getBytes()));
    }

    public static byte[] convert(List<MultipartFile> files) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (MultipartFile f : files) {
            bos.write(f.getBytes());
        }
        return bos.toByteArray();
    }

    private LocalDate getLocalDate(String memoryDate) {
        return LocalDate.parse(memoryDate, DateTimeFormatter.ISO_DATE);
    }
    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
