package com.project.service;

import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.project.exception.HttpException;
import com.project.model.Student;

@Service
public class StudentServiceImpl implements StudentService {
	private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
	private final RestClient restClient;

	public StudentServiceImpl(RestClient restClient) {
		this.restClient = restClient;
	}

	private String getResourcePath() {
		return "/api/studenci";
	}

	private String getResourcePath(Integer id) {
		return String.format("%s/%d", getResourcePath(), id);
	}

	@Override
	public Optional<Student> getStudent(Integer studentId) {
		String path = getResourcePath(studentId);
		logger.info("REQUEST -> GET {}", path);
		Student student = restClient.get()
				.uri(path)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					throw new HttpException(res.getStatusCode(), res.getHeaders());
				})
				.body(Student.class);
		return Optional.ofNullable(student);
	}

	@Override
	public Optional<Student> getStudentByNrIndeksu(String nrIndeksu) {
		String uri = String.format("%s/search?nrIndeksu=%s", getResourcePath(), nrIndeksu);
		logger.info("REQUEST -> GET {}", uri);
		Student student = restClient.get()
				.uri(uri)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					throw new HttpException(res.getStatusCode(), res.getHeaders());
				})
				.body(Student.class);
		return Optional.ofNullable(student);
	}

	@Override
	public Student setStudent(Student student) {
		if (student.getStudentId() != null) {
			String path = getResourcePath(student.getStudentId());
			logger.info("REQUEST -> PUT {}", path);
			restClient.put()
					.uri(path)
					.accept(MediaType.APPLICATION_JSON)
					.body(student)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new HttpException(res.getStatusCode(), res.getHeaders());
					})
					.toBodilessEntity();
			return student;
		} else {
			String path = getResourcePath();
			logger.info("REQUEST -> POST {}", path);
			ResponseEntity<Void> response = restClient.post()
					.uri(path)
					.accept(MediaType.APPLICATION_JSON)
					.body(student)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new HttpException(res.getStatusCode(), res.getHeaders());
					})
					.toBodilessEntity();
			URI location = response.getHeaders().getLocation();
			logger.info("REQUEST (location) -> GET {}", location);
			return restClient.get()
					.uri(location)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new HttpException(res.getStatusCode(), res.getHeaders());
					})
					.body(Student.class);
		}
	}

	@Override
	public void deleteStudent(Integer studentId) {
		String path = getResourcePath(studentId);
		logger.info("REQUEST -> DELETE {}", path);
		restClient.delete()
				.uri(path)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					throw new HttpException(res.getStatusCode(), res.getHeaders());
				})
				.toBodilessEntity();
	}

	@Override
	public Page<Student> getStudenci(Pageable pageable) {
		URI uri = ServiceUtil.getURI(getResourcePath(), pageable);
		logger.info("REQUEST -> GET {}", uri);
		return getPage(uri);
	}

	@Override
	public Page<Student> searchByNrIndeksu(String nrIndeksu, Pageable pageable) {
		URI uri = ServiceUtil.getUriComponent(getResourcePath(), pageable)
				.queryParam("nrIndeksu", nrIndeksu)
				.build().toUri();
		logger.info("REQUEST -> GET {}", uri);
		return getPage(uri);
	}

	@Override
	public Page<Student> searchByNazwisko(String nazwisko, Pageable pageable) {
		URI uri = ServiceUtil.getUriComponent(getResourcePath(), pageable)
				.queryParam("nazwisko", nazwisko)
				.build().toUri();
		logger.info("REQUEST -> GET {}", uri);
		return getPage(uri);
	}

	private Page<Student> getPage(URI uri) {
		return restClient.get()
				.uri(uri.toString())
				.retrieve()
				.body(new ParameterizedTypeReference<RestResponsePage<Student>>() {});
	}
}
