import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import MessageBubble from './MessageBubble';
import LoadingIndicator from'./LoadingIndicator';
import './ChatWindow.css';

const ChatWindow = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [ruleResult, setRuleResult] = useState(null); // 전체 응답 저장
  const [waitingCategory, setWaitingCategory] = useState(false); // 사용자 선택 대기
  const [currentKeyword, setCurrentKeyword] = useState(''); // 현재 검색 키워드 저장
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 번호
  const [waitingForMoreResults, setWaitingForMoreResults] = useState(false); // 더 검색 여부 대기
  const [selectedCategory, setSelectedCategory] = useState(null); // 선택된 카테고리 저장

  const bottomRef = useRef(null);
  useEffect(() => {
    if (bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  // 첫 번째 가이드라인 메시지 추가
  useEffect(() => {
    setMessages((prevMessages) => [
      ...prevMessages,
      { sender: 'system', text: '안녕하세요! 무엇을 도와드릴까요? ' },
      { sender: 'system', text: '가이드 라인은 다음과 같습니다' },
      { sender: 'system', text: '1. 판정 사례 검색: 법률 관련 판례를 검색할 수 있습니다.' },
      { sender: 'system', text: '예시 키워드 및 문장 : 폭언,폭행을 당했어.' },
      { sender: 'system', text: '2. 스마트 검색: 키워드를 입력하면 관련 산업안전보건 법령 등 을 검색할 수 있습니다.' },
      { sender: 'system', text: '예시 키워드 및 문장 : 소화기,석면 ' },
    ]);
  }, []);  // 컴포넌트가 처음 렌더링될 때만 실행되도록 빈 배열 전달

  const handleInputChange = (e) => setInput(e.target.value);

  const handleKeyDown = (e) => {
    if (e.key === 'Enter') {
      if (waitingCategory) {
        handleCategorySelection(input.trim());
      } else if (waitingForMoreResults) {
        handleMoreResultsSelection(input.trim());
      } else {
        sendNormalSearch();
      }
    }
  };

  const appendMessage = (sender, text) => {
    setMessages(prev => [...prev, { sender, text }]);
  };

  const sendNormalSearch = async () => {
    if (!input.trim()) return;

    appendMessage('user', input);

    try {
      const res = await axios.post(
        '/case',
        { keyWord: input},
        { headers: { 'Content-Type': 'application/json' } }
      );

      const dataList = res.data;
      if (!Array.isArray(dataList) || dataList.length === 0) {
        appendMessage('bot', '검색 결과가 없습니다.');
      } else {
        const botMessages = dataList.map(data => {
            const html = `<b>[${data.caseType}]</b> ${data.caseSource} <i>(${data.organ}, ${data.caseTime})</i>`;
            return html;
        });
        botMessages.forEach(msg => appendMessage('bot', msg));
      }
    } catch (err) {
      console.error(err);
      appendMessage('bot', '오류가 발생했습니다.');
    }

    setInput('');
  };

  const sendRuleSearch = async (keyword = null, page = 1, isNewSearch = true) => {
    const searchKeyword = keyword || input.trim();
    
    if (!searchKeyword) return;
    
    if (isNewSearch) {
      appendMessage('user', searchKeyword);
      // 새 검색일 경우 상태 초기화
      setCurrentPage(1);
      setSelectedCategory(null);
    }
    
    setIsLoading(true);
    
    try {
      const res = await axios.post(
        '/rule',
        { keyword: searchKeyword, pageNo: page },
        { headers: { 'Content-Type': 'application/json' } }
      );
  
      const response = res.data.response;
      
      if (response.header.resultCode !== '00') {
        appendMessage('bot', '검색에 실패했습니다.');
        setIsLoading(false);
        return;
      }
      
      const { associated_word, categorycount, items } = response.body;
  
      // 관련 키워드 안내 (첫 페이지에만 표시)
      if (isNewSearch && associated_word?.length > 0) {
        appendMessage('bot', `관련된 키워드: ${associated_word.join(', ')}`);
      }
      
      setIsLoading(false);
      
      // 현재 검색어 저장
      setCurrentKeyword(searchKeyword);
      
      // 전체 항목 저장
      setRuleResult(items);
      
      // 카테고리 메시지 구성 (첫 페이지 또는 카테고리 미선택 상태일 때)
      if (isNewSearch || !selectedCategory) {
        const categoryMap = {
          1: '산업안전보건법',
          2: '산업안전보건법 시행령',
          3: '산업안전보건법 시행규칙',
          4: '산업안전보건 기준에 관한 규칙',
          5: '고시ᆞ훈령ᆞ예규',
          6: '미디어',
          7: 'KOSHA GUIDE',
          8: '중대재해처벌법',
          9: '중대재해처벌법 시행령',
          11: '유해·위험작업의 취업 제한에 관한 규칙'
        };
    
        const availableCategories = Object.entries(categorycount)
          .filter(([key, count]) => count > 0)
          .map(([key, count]) => `${key}: ${categoryMap[key]} (${count}건)`);
    
        appendMessage('bot', `아래 카테고리 중 보고 싶은 번호를 입력해주세요:\n${availableCategories.join('\n')}`);
        
        setWaitingCategory(true);
      } else {
        // 이미 카테고리가 선택된 상태라면 해당 카테고리의 결과만 보여줌
        displayCategoryResults(selectedCategory);
      }
    } catch (err) {
      console.error(err);
      appendMessage('bot', '오류가 발생했습니다.');
      setIsLoading(false);
    }
  
    if (isNewSearch) {
      setInput('');
    }
  };
  
  const handleCategorySelection = (selected) => {
    const category = parseInt(selected);
    if (isNaN(category)) {
      appendMessage('bot', '올바른 숫자를 입력해주세요.');
      return;
    }
    
    setSelectedCategory(category);
    displayCategoryResults(category);
    
    // 카테고리 선택 대기 상태 해제
    setWaitingCategory(false);
    setInput('');
  };
  
  const displayCategoryResults = (category) => {
    if (!ruleResult || !ruleResult.item) {
      appendMessage('bot', '검색 결과가 없습니다.');
      return;
    }
    
    const filtered = ruleResult.item.filter(item => {
      const categoryParsed = parseInt(item.category);
      return !isNaN(categoryParsed) && categoryParsed === category;
    });
    
    if (filtered.length === 0) {
      appendMessage('bot', '선택한 카테고리에 해당하는 결과가 없습니다.');
    } else {
      filtered.forEach(item => {
        const msg = `
          <b>제목:</b> ${item.title}<br/>
          <b>내용:</b> ${item.content}<br/>
          <b>점수:</b> ${item.score}
        `;
        appendMessage('bot', msg);
      });
      
      // 더 검색할지 물어보기
      appendMessage('bot', '더 검색하시겠습니까? (예/아니오)');
      setWaitingForMoreResults(true);
    }
  };
  
  const handleMoreResultsSelection = (answer) => {
    const lowerAnswer = answer.toLowerCase();
    
    if (lowerAnswer === '예' || lowerAnswer === 'y' || lowerAnswer === 'yes') {
      // 다음 페이지 검색
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      
      appendMessage('bot', `${nextPage}페이지 결과를 검색합니다...`);
      sendRuleSearch(currentKeyword, nextPage, false);
    } else {
      // 검색 종료
      appendMessage('bot', '검색을 종료합니다.');
      setCurrentPage(1); // 페이지 번호 초기화
      setCurrentKeyword(''); // 키워드 초기화
      setSelectedCategory(null); // 선택 카테고리 초기화
    }
    
    setWaitingForMoreResults(false);
    setInput('');
  };
  
  // 더 검색하기 버튼 핸들러
  const handleLoadMore = () => {
    if (currentKeyword) {
      const nextPage = currentPage + 1;
      setCurrentPage(nextPage);
      appendMessage('bot', `${nextPage}페이지 결과를 검색합니다...`);
      sendRuleSearch(currentKeyword, nextPage, false);
    }
  };
  
  // 검색 종료 버튼 핸들러
  const handleEndSearch = () => {
    appendMessage('bot', '검색을 종료합니다.');
    setCurrentPage(1); // 페이지 번호 초기화
    setCurrentKeyword(''); // 키워드 초기화
    setSelectedCategory(null); // 선택 카테고리 초기화
    setWaitingForMoreResults(false);
  };

  return (
    <div className="chat-window">
      <div className="messages-container">
        {messages.map((msg, idx) => (
          <MessageBubble key={idx} sender={msg.sender} text={msg.text} />
        ))}
  
        {isLoading && <LoadingIndicator />}
        
        {waitingForMoreResults && (
          <div className="more-results-buttons">
            <button onClick={handleLoadMore}>더 검색하기</button>
            <button onClick={handleEndSearch}>검색 종료</button>
          </div>
        )}
  
        <div ref={bottomRef} />
      </div>
  
      <div className="input-container">
        <input
          type="text"
          value={input}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
          placeholder="메시지를 입력하세요..."
        />
        <button onClick={sendNormalSearch}>판정 사례 검색</button>
        <button onClick={() => sendRuleSearch()}>스마트 검색</button>
      </div>
    </div>
  );
};

export default ChatWindow;