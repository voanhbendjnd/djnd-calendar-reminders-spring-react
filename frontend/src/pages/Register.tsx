import React, { useState } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';

const Register: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values: any) => {
    setLoading(true);
    try {
      const { data } = await api.post('/auth/register', values);
      localStorage.setItem('token', data.token);
      localStorage.setItem('email', data.email);
      message.success('Registered successfully');
      navigate('/dashboard');
    } catch (error: any) {
      message.error(error.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <Card className="w-full max-w-md shadow-lg border-0 rounded-2xl overflow-hidden">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">Create Account</h1>
          <p className="text-gray-500 mt-2">Join FAP Reminders</p>
        </div>
        
        <Form name="register" onFinish={onFinish} layout="vertical" size="large">
          <Form.Item name="email" rules={[{ required: true, message: 'Please input your email!' }, { type: 'email', message: 'Valid email required' }]}>
            <Input prefix={<UserOutlined className="text-gray-400" />} placeholder="Email address" className="rounded-lg" />
          </Form.Item>
          <Form.Item name="password" rules={[{ required: true, message: 'Please input your password!' }, { min: 6, message: 'Password must be at least 6 characters' }]}>
            <Input.Password prefix={<LockOutlined className="text-gray-400" />} placeholder="Password" className="rounded-lg" />
          </Form.Item>
          
          <Form.Item>
            <Button type="primary" htmlType="submit" className="w-full h-12 text-lg rounded-lg bg-green-600 hover:bg-green-700" loading={loading}>
              Sign up
            </Button>
          </Form.Item>
        </Form>
        <div className="text-center text-gray-500">
          Already have an account? <Link to="/login" className="text-blue-600 hover:underline">Log in</Link>
        </div>
      </Card>
    </div>
  );
};

export default Register;
