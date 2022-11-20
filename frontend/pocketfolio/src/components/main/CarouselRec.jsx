import {useRef} from 'react';
import {useState} from 'react';
import {useEffect} from 'react';
import {useNavigate} from 'react-router-dom';
import {
  Container,
  Title,
  CarouselContainer,
  ItemList,
  ItemContainer,
  ImgBox,
  Img,
  NextIcon,
  PrevDiv,
  PrevIcon,
  NextDiv,
  InfoBox,
  Name,
  LikeHitDiv,
  IconDiv,
  LikeIcon,
  Count,
  HitIcon,
  Shadow,
} from './CarouselRec.style';

const CarouselRec = ({rec, idx}) => {
  const navigate = useNavigate();
  const icon = [
    '🧡',
    '💛',
    '💚',
    '💙',
    '💜',
    '🤎',
    '🖤',
    '🤍',
    '💖',
    '💗',
    '💘',
    '💕',
  ];

  const slideRef = useRef();
  const totalRef = useRef();
  const [totalCnt, setTotalCnt] = useState(0);
  const [currentCnt, setCurrentCnt] = useState(0);

  // Next 버튼 클릭 시
  const NextSlide = () => {
    if (currentCnt >= totalCnt) {
      setCurrentCnt(0);
    } else {
      setCurrentCnt(currentCnt + 1);
    }
  };

  // Prev 버튼 클릭 시
  const PrevSlide = () => {
    if (currentCnt === 0) {
      setCurrentCnt(totalCnt); // 마지막 사진으로 넘어갑니다.
    } else {
      setCurrentCnt(currentCnt - 1);
    }
  };

  // 전체 아이템 개수 가져오기
  useEffect(() => {

    if (
      currentCnt === 0 &&
      slideRef.current.scrollWidth <= totalRef.current.clientWidth
    ) {
      setTotalCnt(0);
      setCurrentCnt(0);
    } else {
      setTotalCnt(rec.recommend.length - 1);
    }
  }, [rec, slideRef.current?.scrollWidth, totalRef]);

  useEffect(() => {
    const item = document.querySelector('.item');
    slideRef.current.style.transition = 'all 0.5s ease-in-out';


    if (
      totalRef.current.clientWidth + currentCnt * item.offsetWidth <=
      slideRef.current.scrollWidth + item.offsetWidth
    ) {
      slideRef.current.style.transform = `translateX(-${
        currentCnt * item.offsetWidth
      }px)`;
    } else {
      setCurrentCnt(0);
    }
  }, [currentCnt]);

  return (
    <Container>
      <Title>{`${icon[idx]} "${rec.name}"에서 가장 인기있는 포켓`}</Title>

      <CarouselContainer ref={totalRef}>
        {currentCnt !== 0 && (
          <PrevDiv className="navigation" onClick={PrevSlide}>
            <PrevIcon />
          </PrevDiv>
        )}

        {currentCnt !== totalCnt && (
          <NextDiv className="navigation" onClick={NextSlide}>
            <NextIcon />
          </NextDiv>
        )}

        <ItemList ref={slideRef}>
          {rec.recommend.map((item, idx) => (
            <ItemContainer
              key={idx}
              className="item"
              onClick={() => navigate(`/room/${item.roomSeq}`)}
            >
              <ImgBox>
                <Img
                  src={
                    item.thumbnail
                      ? item.thumbnail
                      : process.env.PUBLIC_URL + '/assets/images/room_01.PNG'
                  }
                  onError={e => {
                    e.target.src =
                      process.env.PUBLIC_URL + '/assets/images/logo3.png';
                  }}
                  alt="썸네일"
                />
                <LikeHitDiv className="icon">
                  <IconDiv>
                    <LikeIcon />
                  </IconDiv>
                  <Count>{item.likeCount}</Count>
                  <IconDiv>
                    <HitIcon />
                  </IconDiv>
                  <Count>{item.hitCount}</Count>
                </LikeHitDiv>
              </ImgBox>

              <InfoBox>
                <Name>{item.roomName}</Name>
              </InfoBox>

              <Shadow className="shadow" />
            </ItemContainer>
          ))}
        </ItemList>
      </CarouselContainer>
    </Container>
  );
};

export default CarouselRec;
