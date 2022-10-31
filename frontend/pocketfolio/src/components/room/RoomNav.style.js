// styled Component
import styled from 'styled-components';

// Navbar
export const Container = styled.div`
  // size
  padding: 0.5rem;
  width: calc(100vw - 1rem);
  height: 3.5rem;

  // position
  z-index: 1;
  position: fixed;
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

// Logo
export const LogoContainer = styled.div`
  left: 1.5rem;
  top: 1.5rem;
  height: 85%;
  cursor: pointer;
`
export const LogoImg = styled.img`
  width: 100%;
  height: 100%;
`;

// Nav Button
export const NavBotton = styled.button`
  padding: 0.5rem 1rem;
  border-radius: 16px;
  border: none;
  background-color: #3c2e9b;
  color: #fff;
  cursor: pointer;

  &:hover {
    background-color: #35297e;
  }

  &:active {
    background-color: #35297e;
    box-shadow: 0.5px 0.5px 0.5px #333;
  }
`;

// LoginDiv
export const LoginDiv = styled.div`
  display: flex;
  align-items: center;
`;
