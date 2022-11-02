import RoomNav from './RoomNav';
import RoomInfo from './RoomInfo';
import Sidebar from './Sidebar';
import Menu from './Menu';
import Toast from './Toast';
import {Container, CanvasWrapper} from './Room.style';
import {Canvas} from '@react-three/fiber';
import {OrbitControls} from '@react-three/drei';
import {useParams} from 'react-router-dom';
import {useState} from 'react';

// 마이룸
const Room = () => {
  // url로 받아온 room_id
  const params = useParams();
  const room_id = parseInt(params.room_id);

  const [sidebar, setSidebar] = useState(false);
  const [toast, setToast] = useState([]);

  const openSidebar = () => {
    setSidebar(true);
  };

  const closeSidebar = () => {
    setSidebar(false);
  };

  // copy to clipboard
  const copyURL = () => {
    window.navigator.clipboard.writeText(
      `https://k7e101.p.ssafy.io/room/${room_id}`,
    );
    addToast('URL이 복사되었습니다');
  };

  // addToast
  const addToast = text => {
    setToast([...toast, text]);
  };

  return (
    <Container className={sidebar ? 'active' : ''}>
      <RoomNav sidebar={sidebar} />
      <RoomInfo sidebar={sidebar} />
      <CanvasWrapper className={sidebar ? 'active' : ''}>
        <Canvas camera={{position: [20, 20, 20], fov: 25}}>
          <OrbitControls autoRotate={false} />
          <gridHelper />
          <axesHelper />
          <mesh>
            <ambientLight intensity={1} castShadow />
            <directionalLight
              position={[10, 10, 10]}
              intensity={1}
              castShadow
            />
            <boxGeometry args={[5, 5, 5]} />
            <meshStandardMaterial attach="material" color={0xa3b18a} />
          </mesh>
        </Canvas>
      </CanvasWrapper>

      {sidebar ? null : (
        <Menu room_id={room_id} openSidebar={openSidebar} copyURL={copyURL} />
      )}

      <Sidebar sidebar={sidebar} closeSidebar={closeSidebar} />
      <Toast toast={toast} />
    </Container>
  );
};


export default Room;
