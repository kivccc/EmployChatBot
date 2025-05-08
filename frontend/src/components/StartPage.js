// src/components/StartPage.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './StartPage.css';
import safetyImage from '../img.png'; // 실제 경로로 바꿀 것
const StartPage = () => {
  const navigate = useNavigate();

  const goToChat = () => {
    navigate('/chat');
  };

  return (
    <div className="start-page">
      <img
        src={safetyImage}
        alt="산업안전 아이콘"
        style={{
            height: '200px',
            verticalAlign: 'bottom',
        }}
      />
<div className="intro-box" style={{
  backgroundColor: '#f4f6f8',
  borderRadius: '10px',
  padding: '20px',
  margin: '30px 0',
  boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
  lineHeight: '1.8',
  fontSize: '0.9rem'
}}>
  <p><strong>고용 정보 챗봇</strong>은 다음과 같은 정보를 제공합니다</p>
  <ul style={{ listStyleType: 'disc', paddingLeft: '20px', marginTop: '15px' }}>
    <li><strong>부당해고 및 노동행위 판정 사례</strong>를 키워드 방식 검색</li>
    <li><strong>산업안전 관련 법령 및 규정</strong>에 대한 스마트 검색</li>
    <li><strong>맞춤형 정보</strong>를 대화형 챗봇을 통해 빠르게 제공</li>
  </ul>
  <p style={{ marginTop: '20px' }}>필요한 정보를 아래 주요 기능을 통해 쉽게 찾아보세요.</p>
</div>

      <div className="features">
        <div className="feature">
          <h2>① 판정 사례 검색</h2>
          <p>복잡한 부당 해고 및 노동행위 관련 판정 사례도 키워드, 상황으로 간단하게 검색 및 확인할 수 있습니다.</p>
        </div>
        <div className="feature">
          <h2>② 스마트 검색</h2>
          <p>키워드를 입력하면 산업안전보건 법령, 규칙, 중대재해 처벌법령, KOSHA GUIDE, 안전보건 미디어 자료, 유해·위험작업의 취업 제한에 관한 규칙 등 필요한 정보를 확인할 수 있습니다.</p>
        </div>
      </div>

      <div className="cta">
        <p>지금 바로 챗봇을 이용해보세요!</p>
        <button onClick={goToChat}>챗봇 시작하기</button>
      </div>
      <footer style={{
  marginTop: '40px',
  paddingTop: '20px',
  borderTop: '1px solid #ccc',
  fontSize: '0.8rem',
  color: '#666',
  textAlign: 'center',
  lineHeight: '1.6'
}}>
  <p>Team : </p>
  <p>문의: <a href="ararstste@gmail.com">ararstste@gmail.com</a></p>
  <p>본 서비스는 제4회 고용노동 공공데이터 활용 공모전에 출품되었습니다.</p>
</footer>

    </div>
  );
};

export default StartPage;
